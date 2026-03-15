package com.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ratelimiter.model.RateLimiterResponse;


@Service
public class RateLimiterService {
	
	// For storing Bucket information corresponding to userId.
	private ConcurrentHashMap<String, TokenBucket> map = new ConcurrentHashMap<>();
	
	// Capacity and Refill rate for user
	private final int capacity = 10;
	private final int refillRate = 5;
	private final String SUCCESS_MESSAGE = "Your request is successfull!";
	private final String REFILL_MESSAGE = "Token count is zero! \n initializing Token Refill!";
	private final String USER_NOT_FOUND = "User Not found for userId: ";
	private final String NEW_USER_CREATED = "New user created with userId: ";
	private final String TOKEN_FREEZE = "Token count exhaust, Wait for 5 second for next request for userId: ";
	
	
	public RateLimiterResponse checkLimit(String userId) {
		
		RateLimiterResponse rateLimiterResponse;
		
		// If user exist then checking available tokens
		if (map.get(userId) != null) {
			TokenBucket bucket = map.get(userId);
			int currentTokenNumber = bucket.getTokens();
			if (currentTokenNumber>1) {
				bucket.setTokens(currentTokenNumber-1);
				map.put(userId, bucket);
				rateLimiterResponse = new RateLimiterResponse(true, currentTokenNumber-1, SUCCESS_MESSAGE);
			} else if (currentTokenNumber == 1) {
				bucket.setTokens(0);
				map.put(userId, bucket);
				// Note if allowed request is true and token remain is zero then it was last request
				rateLimiterResponse = new RateLimiterResponse(true, currentTokenNumber-1, REFILL_MESSAGE);
				
				// In future we can implement refill mechanism
			} else {
				
				rateLimiterResponse = new RateLimiterResponse(false, 0, TOKEN_FREEZE + userId);
			}
		} else {
			// This condition for user not found and condition where request is freeze because token exhaust and timeout both give same response
			// We need to fix to make it differentiate, probably proper message field
			rateLimiterResponse = new RateLimiterResponse(false, 0, USER_NOT_FOUND + userId);
		}
		
		return rateLimiterResponse;
	}
	
	public RateLimiterResponse creatingBucketForNewUserId(String userId) {
		RateLimiterResponse rateLimiterResponse;
		
		// Updating Bucket for new userId. 
		long currentMillis = System.currentTimeMillis();
		TokenBucket newUserBucket = new TokenBucket(currentMillis, capacity);
		map.put(userId, newUserBucket);
		
		//Since the current token is equal to capacity of bucket
		rateLimiterResponse =  new RateLimiterResponse(true, capacity, NEW_USER_CREATED + userId);
		
		return rateLimiterResponse;
	}

}
