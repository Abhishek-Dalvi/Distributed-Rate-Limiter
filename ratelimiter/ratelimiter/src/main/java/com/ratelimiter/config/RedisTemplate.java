package com.ratelimiter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisTemplate {

	@Bean
	public JedisPool jedisPool(@Value("${spring.redis.host:localhost}") String host, @Value("${spring.redis.port:6379}") int port) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(10);
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(1);
		poolConfig.setJmxEnabled(false);
		return new JedisPool(poolConfig, host, port);
	}
}
