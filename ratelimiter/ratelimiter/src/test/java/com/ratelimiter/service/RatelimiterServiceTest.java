package com.ratelimiter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.ratelimiter.model.RateLimiterResponse;

public class RatelimiterServiceTest {
	
	@Test
	void ratelimiterServiceTest() throws InterruptedException {
		RateLimiterService rateLimiterService = new RateLimiterService();
		RateLimiterResponse rateLimiterResponse;
		
		// Checking for new user till bucket limit exhaust
		for(int i=4; i>-1; i--) {
			rateLimiterResponse = rateLimiterService.checkLimit("user_abc");
			assertEquals(i, rateLimiterResponse.getRemainingToken());
			assertEquals("Your request is successfull!", rateLimiterResponse.getMessageString());
		}
		
		// Checking for request after bucket limit exhaust
		assertEquals("Token count exhaust, Wait for 10 second for next request for userId: user_abc", rateLimiterService.checkLimit("user_abc").getMessageString());
		
		// Sleeping time for 10 seconds for refill
		Thread.sleep(10001L);
		rateLimiterResponse = rateLimiterService.checkLimit("user_abc");
		
		assertEquals("Your request is successfull!", rateLimiterResponse.getMessageString());
		assertEquals(0, rateLimiterResponse.getRemainingToken());
		
	}

}
