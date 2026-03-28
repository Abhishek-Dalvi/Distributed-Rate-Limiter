package com.ratelimiter.service;

import java.time.Clock;
import java.time.Instant;

public final class TimeProvider {
	private static Clock clock = Clock.systemUTC();

	public static long currentTimeMillis() {
		return Instant.now(clock).toEpochMilli();
	}

	// Restrict visibility: only accessible in tests
	static void setClockForTesting(Clock newClock) {
		clock = newClock;
	}

}
