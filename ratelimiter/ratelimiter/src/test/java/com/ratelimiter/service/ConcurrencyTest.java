package com.ratelimiter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.ratelimiter.model.RateLimiterResponse;

public class ConcurrencyTest {
	
	@RepeatedTest(20)
	@Execution(ExecutionMode.SAME_THREAD)
	void checkingRaceConditionTest() throws Exception {
		RateLimiterService rateLimiterService = new RateLimiterService();
		ExecutorService executorService = Executors.newFixedThreadPool(50);
		
        // Shared counter (thread-safe)
        AtomicInteger counter = new AtomicInteger(0);
		
		Runnable task = () -> {
			
            try {
            	RateLimiterResponse rateLimiterResponse = rateLimiterService.checkLimit("user_abc");
            	if(rateLimiterResponse.isAllowed()) {
            		counter.incrementAndGet();
            	}
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
		};
		
		for (int i = 0; i < 50; i++) {
			executorService.submit(task);
		}
		
		// Shutdown executor (no new tasks accepted, existing tasks finish)
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(5, counter.get());

	}

}
