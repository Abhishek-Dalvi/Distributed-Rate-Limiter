package com.ratelimiter.service.impl;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ratelimiter.model.RateLimiterResponse;

@SpringBootTest
public class RedisLuaExampleTest {
	
	private final RedisLuaExample redisLuaExample;
	
	@Autowired
	public RedisLuaExampleTest(RedisLuaExample redisLuaExample) {
		this.redisLuaExample = redisLuaExample;
	}

	@Test
	void checkingRedisScript() throws IOException {
		
		
		for(int i=0; i<6; i++) {
			RateLimiterResponse rateLimiterResponse =  redisLuaExample.redisLuaExecutionReturn("user_abc");
			System.out.println("is allowed:" + rateLimiterResponse.isAllowed() + " remaining token: " + rateLimiterResponse.getRemainingToken());
		}
		
	}

}
