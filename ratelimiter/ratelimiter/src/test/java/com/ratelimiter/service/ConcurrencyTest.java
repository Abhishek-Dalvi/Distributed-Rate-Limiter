package com.ratelimiter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.impl.InMemoryRateLimiterService;

public class ConcurrencyTest {
	
	@RepeatedTest(500)
	@Execution(ExecutionMode.SAME_THREAD)
	void checkingRaceConditionTest() throws Exception {
		InMemoryRateLimiterService rateLimiterService = new InMemoryRateLimiterService();
		int threadCount = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		
		// Latch to wait until all thread start
		CountDownLatch startLatch = new CountDownLatch(1);
		
		// Latch to wait until all threads finish
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
		
        // Shared counter (thread-safe)
        AtomicInteger counter = new AtomicInteger(0);
		
		Runnable task = () -> {
			
            try {
            	// Waiting to start multiple thread at a same time.
            	startLatch.await();
            	RateLimiterResponse rateLimiterResponse = rateLimiterService.checkLimit("user_abc");
            	if(rateLimiterResponse.isAllowed()) {
            		counter.incrementAndGet();
            	}
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
				doneLatch.countDown();
			}
		};
		
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(task);
		}
		
		// Release all threads at once
        startLatch.countDown();
        
        // Wait until all threads finish
        doneLatch.await();
		
		// Shutdown executor (no new tasks accepted, existing tasks finish)
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(5, counter.get());

	}

}
