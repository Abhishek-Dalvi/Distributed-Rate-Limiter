package com.ratelimiter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.impl.InMemoryRateLimiterService;
import com.ratelimiter.service.impl.RedisRateLimiterService;

import redis.clients.jedis.JedisPool;

public class RatelimiterServiceTest {
	
	@Test
	void ratelimiterServiceTest() throws Exception {
//		InMemoryRateLimiterService rateLimiterService = new InMemoryRateLimiterService();
		
		JedisPool jedisPool = new JedisPool();
		
		RedisRateLimiterService rateLimiterService = new RedisRateLimiterService(jedisPool);
		RateLimiterResponse rateLimiterResponse;
		
		// Step 1: Freeze time at 2026-03-28T10:00:00Z
        Clock timeClock = Clock.fixed(Instant.parse("2026-03-28T10:00:00Z"), ZoneOffset.UTC);
        
        TimeProvider.setClockForTesting(timeClock);
		
		// Checking for new user till bucket limit exhaust
		for(int i=4; i>-1; i--) {
			rateLimiterResponse = rateLimiterService.checkLimit("user_abc");
			assertEquals(i, rateLimiterResponse.getRemainingToken());
			assertEquals("Your request is successfull!", rateLimiterResponse.getMessageString());
		}
		
		// Checking for request after bucket limit exhaust
		assertEquals("Token count exhaust, Wait for 10 second for next request for userId: user_abc", rateLimiterService.checkLimit("user_abc").getMessageString());
		
		// Step 2: Advance by 11 seconds
		Clock timeClockAfter = Clock.fixed(Instant.parse("2026-03-28T10:00:11Z"), ZoneOffset.UTC);
		
		TimeProvider.setClockForTesting(timeClockAfter);
		rateLimiterResponse = rateLimiterService.checkLimit("user_abc");
		
		assertEquals("Your request is successfull!", rateLimiterResponse.getMessageString());
		assertEquals(0, rateLimiterResponse.getRemainingToken());
		
	}
	
}
