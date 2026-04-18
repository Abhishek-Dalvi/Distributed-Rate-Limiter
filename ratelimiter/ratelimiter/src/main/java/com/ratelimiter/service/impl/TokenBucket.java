package com.ratelimiter.service.impl;

import com.ratelimiter.service.TimeProvider;

class TokenBucket {
	
	private int capacity;
	private long lastRefillTimestamp;
	private int tokens;
	private int refillRate;
	
	public TokenBucket(int capacity, int refillRate) {
		this.lastRefillTimestamp = TimeProvider.currentTimeMillis();
		this.tokens = capacity;
		this.capacity = capacity;
		this.refillRate = refillRate;
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
		int tokenToAddAsPerRate = (int) (elapsedTime/refillRate);
		int previousTokenCounts = getTokens();
		int remainingToken = capacity - previousTokenCounts;
		int finalTokenToAdd = Math.min(tokenToAddAsPerRate, remainingToken);
		
		// We are updating only if there is token update happens. 
		if(finalTokenToAdd>0) {
			// Considering last refill timestamp as least of token to add basis
			// We need to be consistent by adding exact refill timestamp. 
			setLastRefillTimestamp(TimeProvider.currentTimeMillis());
		}
		
		setTokens(finalTokenToAdd + previousTokenCounts);
	}

}
