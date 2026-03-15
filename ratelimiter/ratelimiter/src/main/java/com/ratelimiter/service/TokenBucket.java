package com.ratelimiter.service;

class TokenBucket {
//	private final int capacity = 10;
//	private final int refillRate = 5;
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
	long getLastRefillTimestampDate() {
		return lastRefillTimestampDate;
	}
	void setLastRefillTimestampDate(long lastRefillTimestampDate) {
		this.lastRefillTimestampDate = lastRefillTimestampDate;
	}
	int getTokens() {
		return tokens;
	}
	void setTokens(int tokens) {
		this.tokens = tokens;
	}

}
