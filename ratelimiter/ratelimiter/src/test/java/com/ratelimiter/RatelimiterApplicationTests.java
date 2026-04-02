package com.ratelimiter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import redis.clients.jedis.Jedis;

@SpringBootTest
class RatelimiterApplicationTests {

	@Test
	void contextLoads() {
		
	try (Jedis jedis = new Jedis("localhost", 6379)) {
			// Store a key-value pair
	        jedis.set("myKey", "Hello Redis!");
	        
	        // Retrieve the value
	        String value = jedis.get("myKey");
	        
	        assertEquals("Hello Redis!", value);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

}
