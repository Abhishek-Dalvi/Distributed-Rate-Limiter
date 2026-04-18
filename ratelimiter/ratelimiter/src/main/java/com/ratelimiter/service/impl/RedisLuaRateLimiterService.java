package com.ratelimiter.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.RateLimiterServiceInterface;
import com.ratelimiter.service.TimeProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service("redisLuaRateLimiterService")
public class RedisLuaRateLimiterService implements RateLimiterServiceInterface {
	
	private static final Logger log = LoggerFactory.getLogger(RedisLuaRateLimiterService.class);
	
	public final JedisPool jedisPool;
	
	@Value("${bucket.capacity:5}")
	private int bucketCapacity;
	
	@Value("${bucket.refillTime:10000}")
	private int bucketRefillTime;
	
	@Value("${bucket.keyExpiration:3600}")
	private int bucketKeyExpiration;
	
	@Autowired
	public RedisLuaRateLimiterService(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public RateLimiterResponse checkLimit(String userId) throws IOException {
		
		RateLimiterResponse rateLimiterResponse;
		try(Jedis jedis = jedisPool.getResource()){
			// Loading Lua script
			InputStream stream = new ClassPathResource("scripts/check_a.lua").getInputStream();;
			String script = new Scanner(stream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
			
			// Load script into Redis and get SHA1
            String sha = jedis.scriptLoad(script);
            
            long currentMillis = TimeProvider.currentTimeMillis();
            
            Object bucketInfo = jedis.evalsha(sha, 1, userId, String.valueOf(currentMillis), String.valueOf(bucketCapacity), String.valueOf(bucketRefillTime), String.valueOf(bucketKeyExpiration));
            
            List<Object> responseValues = (List<Object>) bucketInfo;
            
            boolean success = (responseValues.get(0) instanceof Long && ((Long) responseValues.get(0)) == 1L);
            
            // Second element: could be integer or float
            Object tokenObj = responseValues.get(1);
            double tokenNumber;

            if (tokenObj instanceof Long) {
                tokenNumber = ((Long) tokenObj).doubleValue(); // convert integer to double
            } else if (tokenObj instanceof String) {
                tokenNumber = Double.parseDouble((String) tokenObj); // parse float string
            } else {
                throw new IllegalStateException("Unexpected type: " + tokenObj.getClass());
            }
            int tokenNumberInt = (int) tokenNumber;
            String message = (String) responseValues.get(2);
            
            rateLimiterResponse = new RateLimiterResponse(success, tokenNumberInt, message);
            
		} 
		
		log.info("For user Id: " + userId + " " + rateLimiterResponse);
		
		return rateLimiterResponse;
		
	}

}
