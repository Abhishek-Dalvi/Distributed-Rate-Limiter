package com.ratelimiter.service.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.RateLimiterServiceInterface;
import com.ratelimiter.service.TimeProvider;


@Service("inMemoryRateLimiterService")
public class InMemoryRateLimiterService implements RateLimiterServiceInterface {
	
	private static final Logger log = LoggerFactory.getLogger(InMemoryRateLimiterService.class);
	
	// For storing Bucket information corresponding to userId.
	private Cache<String, TokenBucket> bucketcache = Caffeine.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build();
		
	@Value("${bucket.capacity:5}")
	private int bucketCapacity;
	
	@Value("${bucket.refillTime:10000}")
	private int bucketRefillTime;
	
	// Capacity and Refill rate for user
	private final String SUCCESS_MESSAGE = "Your request is successfull!";
	private final String TOKEN_FREEZE = "Token count exhaust, Wait for 10 second for next request for userId: ";

	@Override
	public RateLimiterResponse checkLimit(String userId) throws Exception{
		
		RateLimiterResponse rateLimiterResponse;
		// Creating bucket for user if doesn't exist.
		bucketcache.asMap().computeIfAbsent(userId, k -> new TokenBucket(bucketCapacity, bucketRefillTime));
		
		//This gave reference to bucket object call by address or memory
		TokenBucket bucket = bucketcache.asMap().get(userId);
		
		synchronized (bucket) {
			// Refill logic comes first.
			long currentMillis = TimeProvider.currentTimeMillis();
			long elapseTime = currentMillis - bucket.getLastRefillTimestamp();
			// Minimum time required to refill atleast one token is 10000 milliseconds
			if (elapseTime >=bucketRefillTime) {
				bucket.refillBucket(elapseTime);
			}
			int currentTokenNumber = bucket.getTokens();
			if (currentTokenNumber>0) {
				rateLimiterResponse = new RateLimiterResponse(true, currentTokenNumber-1, SUCCESS_MESSAGE);
				// Updating bucket after setting a response 
				bucket.setTokens(currentTokenNumber-1);
			} else {
				
				rateLimiterResponse = new RateLimiterResponse(false, 0, TOKEN_FREEZE + userId);
			}
		}
		
		log.info("For user Id: " + userId + " " + rateLimiterResponse);
		
		return rateLimiterResponse;
	}

}
