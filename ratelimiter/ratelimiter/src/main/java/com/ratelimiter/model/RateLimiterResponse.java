package com.ratelimiter.model;

public class RateLimiterResponse {
	private boolean allowed;
	private int remainingToken;
	/**
	 * @param allowed
	 * @param remainingToken
	 */
	public RateLimiterResponse(boolean allowed, int remainingToken) {
		super();
		this.allowed = allowed;
		this.remainingToken = remainingToken;
	}
	public boolean isAllowed() {
		return allowed;
	}

	public int getRemainingToken() {
		return remainingToken;
	}	

}
