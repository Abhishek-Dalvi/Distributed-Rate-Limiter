package com.ratelimiter.service;

class TokenBucket {
	private final int capacity = 5;
	private long lastRefillTimestamp;
	private int tokens;
	/**
	 * @param lastRefillTimestampDate
	 * @param tokens
	 */
	TokenBucket(long lastRefillTimestampDate, int tokens) {
		this.lastRefillTimestamp= lastRefillTimestampDate;
		this.tokens = tokens;
	}
	long getLastRefillTimestamp() {
		return lastRefillTimestamp;
	}
	void setLastRefillTimestamp(long lastRefillTimestamp) {
		this.lastRefillTimestamp= lastRefillTimestamp;
	}
	int getTokens() {
		return tokens;
	}
	void setTokens(int tokens) {
		this.tokens = tokens;
	}
	
	public void refillBucket(long elapsedTime) {
		// This gives token to add based on rate and can exceeds capacity
		int tokenToAddAsPerRate = (int) (elapsedTime/10000);
		int previousTokenCounts = getTokens();
		int remainingToken = capacity - previousTokenCounts;
		int finalTokenToAdd = Math.min(tokenToAddAsPerRate, remainingToken);
		
		// We are updating only if there is token update happens. 
		if(finalTokenToAdd>0) {
			// Considering last refill timestamp as least of token to add basis
			// We need to be consistent by adding exact refill timestamp. 
			setLastRefillTimestamp(System.currentTimeMillis());
		}
		
		setTokens(finalTokenToAdd + previousTokenCounts);
	}

}
