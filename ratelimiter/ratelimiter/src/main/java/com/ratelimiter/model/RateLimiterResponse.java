package com.ratelimiter.model;

public class RateLimiterResponse {
	private boolean allowed;
	private int remainingToken;
	private String messageString;
	/**
	 * @param allowed
	 * @param remainingToken
	 */
	public RateLimiterResponse(boolean allowed, int remainingToken, String messageString) {
		this.allowed = allowed;
		this.remainingToken = remainingToken;
		this.messageString = messageString;
	}
	public boolean isAllowed() {
		return allowed;
	}

	public int getRemainingToken() {
		return remainingToken;
	}
	public String getMessageString() {
		return messageString;
	}
	@Override
	public String toString() {
		return "Is request allowed: " + allowed + ", remaining token counts are: " + remainingToken + ", message is: "
				+ messageString;
	}
	
	

}
