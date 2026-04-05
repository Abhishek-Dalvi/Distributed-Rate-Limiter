package com.ratelimiter.redistests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import redis.clients.jedis.Jedis;

public class RedisConnectionTest {
	@Test
	void checkingRedisConnection() {
		try (Jedis jedis = new Jedis("localhost", 6379)) {
			// Store a key-value pair
			jedis.set("myKey", "Hello Redis!");

			// Retrieve the value
			String value = jedis.get("myKey");

			assertEquals("Hello Redis!", value);
		} catch (Exception e) {
			fail("Exception with error: " + e.getMessage());
		}
	}
}
