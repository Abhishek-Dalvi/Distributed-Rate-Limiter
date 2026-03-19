package com.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ratelimiter.model.RateLimiterResponse;


@Service
public class RateLimiterService {
	
	// For storing Bucket information corresponding to userId.
	private ConcurrentHashMap<String, TokenBucket> map = new ConcurrentHashMap<>();
	
	// Capacity and Refill rate for user
	private final int capacity = 5;
	private final String SUCCESS_MESSAGE = "Your request is successfull!";
	private final String USER_NOT_FOUND = "User Not found for userId: ";
	private final String NEW_USER_CREATED = "New user created with userId: ";
	private final String TOKEN_FREEZE = "Token count exhaust, Wait for 1 second for next request for userId: ";
	
	
	public RateLimiterResponse checkLimit(String userId) {
		
		RateLimiterResponse rateLimiterResponse;
		
		// Refill logic comes first.
		long currentMillis = System.currentTimeMillis();
		
//					map.putIfAbsent(userId, new TokenBucket(currentMillis, capacity));
		map.computeIfAbsent(userId, k -> new TokenBucket(currentMillis, capacity));
		
		//This gave referrence to bucket object call by address or memory
		TokenBucket bucket = map.get(userId);
		
		synchronized (bucket) {
			// Refill logic comes first.
//						long currentMillis = System.currentTimeMillis();
			long elapseTime = currentMillis - bucket.getLastRefillTimestamp();
			// Minimum time required to refill atleast one token is 1000 milliseconds
			if (elapseTime >10000) {
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
		
		// If user exist then checking available tokens
//		if (map.get(userId) != null) {
//			// Refill logic comes first.
//			long currentMillis = System.currentTimeMillis();
//			
////			map.putIfAbsent(userId, new TokenBucket(currentMillis, capacity));
//			map.computeIfAbsent(userId, k -> new TokenBucket(currentMillis, capacity));
//			
//			//This gave referrence to bucket object call by address or memory
//			TokenBucket bucket = map.get(userId);
//			
//			synchronized (bucket) {
//				// Refill logic comes first.
////				long currentMillis = System.currentTimeMillis();
//				long elapseTime = currentMillis - bucket.getLastRefillTimestamp();
//				// Minimum time required to refill atleast one token is 1000 milliseconds
//				if (elapseTime >10000) {
//					bucket.refillBucket(elapseTime);
//				}
//				int currentTokenNumber = bucket.getTokens();
//				if (currentTokenNumber>0) {
//					rateLimiterResponse = new RateLimiterResponse(true, currentTokenNumber-1, SUCCESS_MESSAGE);
//					// Updating bucket after setting a response 
//					bucket.setTokens(currentTokenNumber-1);
//				} else {
//					
//					rateLimiterResponse = new RateLimiterResponse(false, 0, TOKEN_FREEZE + userId);
//				}
//			}
//			
//		} else {
//			// This condition for user not found.
//			rateLimiterResponse = new RateLimiterResponse(false, 0, USER_NOT_FOUND + userId);
//		}
		
		return rateLimiterResponse;
	}
	
//	private TokenBucket creatingBucket(String userId) {
////		RateLimiterResponse rateLimiterResponse;
//		
//		// Updating Bucket for new userId. 
//		long currentMillis = System.currentTimeMillis();
//		TokenBucket newUserBucket = new TokenBucket(currentMillis, capacity);
////		map.put(userId, newUserBucket);
//		
//		//Since the current token is equal to capacity of bucket
////		rateLimiterResponse =  new RateLimiterResponse(true, capacity, NEW_USER_CREATED + userId);
//		
//		return newUserBucket;
//	}
	

}
