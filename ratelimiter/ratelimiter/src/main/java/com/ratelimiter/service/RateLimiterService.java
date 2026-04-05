package com.ratelimiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ratelimiter.model.RateLimiterResponse;


@Service
public class RateLimiterService {
	
	private final RateLimiterServiceInterface rateLimiterServiceInterface;
	
	@Autowired //Keeping autowired for understanding. for single constructor it's not needed, it implies.
	public RateLimiterService(@Qualifier("redisRateLimiterService") RateLimiterServiceInterface rateLimiterServiceInterface) {
		this.rateLimiterServiceInterface = rateLimiterServiceInterface;
	}
	
	public RateLimiterResponse checkLimit(String userId) {
		
		return rateLimiterServiceInterface.checkLimit(userId);
	}
	
}
