package com.ratelimiter.service;

import java.util.concurrent.ConcurrentHashMap;

import com.ratelimiter.model.RateLimiterResponse;

public class RateLimiterService {
	
	private ConcurrentHashMap<String, TokenBucket> map = new ConcurrentHashMap<>();
	
	public RateLimiterResponse checkLimit(String userId) {
		// TODO Auto-generated method stub
		if (map.get(userId) != null) {
			
		}
		RateLimiterResponse rateLimiterResponse = new RateLimiterResponse(true, 10);
		return rateLimiterResponse;
	}
//	
//	ConcurrentHashMap<String, TokenBucket> map = new ConcurrentHashMap<>();
//	
//	if(boolean True):
		
		

}
