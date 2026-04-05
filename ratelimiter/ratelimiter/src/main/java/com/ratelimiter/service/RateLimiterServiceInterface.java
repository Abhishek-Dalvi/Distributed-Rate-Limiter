package com.ratelimiter.service;

import com.ratelimiter.model.RateLimiterResponse;

public interface RateLimiterServiceInterface {
	
	RateLimiterResponse checkLimit(String userId);

}
