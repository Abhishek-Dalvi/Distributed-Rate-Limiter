package com.ratelimiter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.impl.RedisRateLimiterService;

import redis.clients.jedis.JedisPool;

@SpringBootTest
public class RatelimiterServiceTest {
	
	private final RedisRateLimiterService redisRateLimiterService;
	
	@Autowired
	public RatelimiterServiceTest(RedisRateLimiterService redisRateLimiterService) {
		this.redisRateLimiterService = redisRateLimiterService;
	}
	
	@Test
	void ratelimiterServiceTest() throws Exception {
		
		JedisPool jedisPool = new JedisPool();
		
		RateLimiterResponse rateLimiterResponse;
		
		// Step 1: Freeze time at 2026-03-28T10:00:00Z
        Clock timeClock = Clock.fixed(Instant.parse("2026-03-28T10:00:00Z"), ZoneOffset.UTC);
        
        TimeProvider.setClockForTesting(timeClock);
        
        String userId = UUID.randomUUID().toString();
		
		// Checking for new user till bucket limit exhaust
		for(int i=4; i>-1; i--) {
			rateLimiterResponse = redisRateLimiterService.checkLimit(userId);
			assertEquals(i, rateLimiterResponse.getRemainingToken());
			assertEquals("Your request is successfull!", rateLimiterResponse.getMessageString());
		}
		
		// Checking for request after bucket limit exhaust
		assertEquals("Token count exhaust, Wait for 10 second for next request for userId: "+userId, redisRateLimiterService.checkLimit(userId).getMessageString());
		
		// Step 2: Advance by 11 seconds
		Clock timeClockAfter = Clock.fixed(Instant.parse("2026-03-28T10:00:11Z"), ZoneOffset.UTC);
		
		TimeProvider.setClockForTesting(timeClockAfter);
		rateLimiterResponse = redisRateLimiterService.checkLimit(userId);
		
		assertEquals("Your request is successfull!", rateLimiterResponse.getMessageString());
		assertEquals(0, rateLimiterResponse.getRemainingToken());
		
	}
	
}
