package com.ratelimiter.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.TimeProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service("redisLuaRateLimiterService")
public class RedisLuaRateLimiterService {
	public final JedisPool jedisPool;
	
	@Autowired
	public RedisLuaRateLimiterService(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public RateLimiterResponse checkLimit(String userId) throws IOException {
		RateLimiterResponse rateLimiterResponse;
		try(Jedis jedis = jedisPool.getResource()){
			// Loading Lua script
			InputStream stream = new ClassPathResource("scripts/check_a.lua").getInputStream();;
			String script = new Scanner(stream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
			
			// Load script into Redis and get SHA1
            String sha = jedis.scriptLoad(script);
            
            long currentMillis = TimeProvider.currentTimeMillis();
            
            Object bucketInfo = jedis.evalsha(sha, 1, userId, String.valueOf(currentMillis));
            
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
		
		return rateLimiterResponse;
		
	}

}
