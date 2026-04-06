package com.ratelimiter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.impl.RedisRateLimiterService;

@SpringBootTest
public class ConcurrencyTest {
	
	private final RedisRateLimiterService rateLimiterService;
	
	// Constructor injection — Spring will supply the bean
    @Autowired
    ConcurrencyTest(RedisRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }
	
	
	@RepeatedTest(5)
	@Execution(ExecutionMode.SAME_THREAD)
	void checkingRaceConditionTest() throws Exception {
//		InMemoryRateLimiterService rateLimiterService = new InMemoryRateLimiterService();
		
//		JedisPool jedisPool = new JedisPool();
//		
//		RedisRateLimiterService rateLimiterService = new RedisRateLimiterService(jedisPool);
		
		
		
		int threadCount = 50;
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
            	String userId = UUID.randomUUID().toString();
            	startLatch.await();
            	RateLimiterResponse rateLimiterResponse = rateLimiterService.checkLimit(userId);
            	if(rateLimiterResponse.isAllowed()) {
            		counter.incrementAndGet();
            	}
            } catch (Exception e) {
                System.err.println("error is: " + e.getMessage());
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
