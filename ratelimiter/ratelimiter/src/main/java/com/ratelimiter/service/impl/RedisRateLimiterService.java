package com.ratelimiter.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.RateLimiterServiceInterface;
import com.ratelimiter.service.TimeProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Service("redisRateLimiterService")
public class RedisRateLimiterService implements RateLimiterServiceInterface {
	
	private static final Logger log = LoggerFactory.getLogger(RedisRateLimiterService.class);
	
	@Value("${bucket.capacity:5}")
	private int bucketCapacity;
	
	@Value("${bucket.refillTime:10000}")
	private int bucketRefillTime;
	
	private final JedisPool jedisPool;
	
	@Autowired //Keeping autowired for understanding. for single constructor it's not needed, it implies.
	public RedisRateLimiterService(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	// Capacity and Refill rate for user
	private final String SUCCESS_MESSAGE = "Your request is successfull!";
	private final String TOKEN_FREEZE = "Token count exhaust, Wait for 10 second for next request for userId: ";

	@Override
	public RateLimiterResponse checkLimit(String userId) throws Exception {
		RateLimiterResponse rateLimiterResponse;
		
		try (Jedis jedis = jedisPool.getResource()) {
			
			String userBucketString = jedis.get(userId);
			long currentMillis = TimeProvider.currentTimeMillis();
			
			//Step 1: Bucket creation for new user in Redis
			if (userBucketString == null) {
				String newBucketString = bucketCapacity + "," + currentMillis;
				jedis.set(userId, newBucketString);
				userBucketString = newBucketString;
			}
			
			String[] bucketInfo =  userBucketString.split(",");
			
			// These two parameters only use for decision 
			int currentTokenNumber = Integer.parseInt(bucketInfo[0]);
			long lastRefillTimestamp = Long.parseLong(bucketInfo[1]);
			
			//Step2: Refill logic comes first.
			long elapseTime = currentMillis - lastRefillTimestamp;
			
			String updatedUserBucketString;
			// Minimum time required to refill atleast one token is 10000 milliseconds
			if (elapseTime >=bucketRefillTime) {
				// Number of token after lazy update as per refill rate
				int effectiveTokenAsPerRate = currentTokenNumber + (int) (elapseTime/bucketRefillTime);
				
				// Number of token after considering bucket capacity and consuming 1 token
				currentTokenNumber = Math.min(5, effectiveTokenAsPerRate);
				
				// lastRefillTimestamp become now
				lastRefillTimestamp = currentMillis;
			}
			
			if (currentTokenNumber>0) {
				// Consuming 1 token
				currentTokenNumber = currentTokenNumber -1;
				rateLimiterResponse = new RateLimiterResponse(true, currentTokenNumber, SUCCESS_MESSAGE);
				// Updating bucket after setting a response 
				updatedUserBucketString = currentTokenNumber + "," + lastRefillTimestamp;
				// Updating bucket only if we are consuming token
				jedis.set(userId, updatedUserBucketString);
				
			} else {
				
				rateLimiterResponse = new RateLimiterResponse(false, 0, TOKEN_FREEZE + userId);
			}
		}
		
		log.info("For user Id: " + userId + " " + rateLimiterResponse);
		
		return rateLimiterResponse;
	}

}
