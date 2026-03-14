package com.ratelimiter.service;

class TokenBucket {
	private final int capacity = 10;
	private final int refillRate = 5;
	private long lastRefillTimestampDate;
	private int tokens;
	/**
	 * @param lastRefillTimestampDate
	 * @param tokens
	 */
	TokenBucket(long lastRefillTimestampDate, int tokens) {
		this.lastRefillTimestampDate = lastRefillTimestampDate;
		this.tokens = tokens;
	}
	

}
