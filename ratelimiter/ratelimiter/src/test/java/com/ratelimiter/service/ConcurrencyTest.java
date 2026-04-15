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
import com.ratelimiter.service.impl.InMemoryRateLimiterService;
import com.ratelimiter.service.impl.RedisLuaRateLimiterService;
import com.ratelimiter.service.impl.RedisRateLimiterService;


@SpringBootTest
public class ConcurrencyTest {
	
	private final RedisRateLimiterService redisRateLimiterService;
	
	private final RedisLuaRateLimiterService redisLuaRateLimiterService;

	// Constructor injection — Spring will supply the bean
    @Autowired
    ConcurrencyTest(RedisRateLimiterService redisRateLimiterService, RedisLuaRateLimiterService redisLuaRateLimiterService) {
        this.redisRateLimiterService = redisRateLimiterService;
        this.redisLuaRateLimiterService = redisLuaRateLimiterService;
    }
    
    @RepeatedTest(500)
	@Execution(ExecutionMode.SAME_THREAD)
	void checkingRaceCondition() throws Exception {
		
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		
		// Latch to wait until all thread start
		CountDownLatch startLatch = new CountDownLatch(1);
		
		// Latch to wait until all threads finish
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
		
        // Shared counter (thread-safe)
        AtomicInteger counter = new AtomicInteger(0);
        
        String userId = UUID.randomUUID().toString();
        
		Runnable task = () -> {
			
            try {
            	// Waiting to start multiple thread at a same time.
            	
            	startLatch.await();
//            	RateLimiterResponse rateLimiterResponse = redisRateLimiterService.checkLimit(userId);
            	RateLimiterResponse rateLimiterResponse = redisLuaRateLimiterService.checkLimit(userId);
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
    
    
	
	@RepeatedTest(5)
	@Execution(ExecutionMode.SAME_THREAD)
	void checkingRaceConditionForInMemory() throws Exception {
		InMemoryRateLimiterService inMemoryRateLimiterService = new InMemoryRateLimiterService();
		
		
		int threadCount = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		
		// Latch to wait until all thread start
		CountDownLatch startLatch = new CountDownLatch(1);
		
		// Latch to wait until all threads finish
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
		
        // Shared counter (thread-safe)
        AtomicInteger counter = new AtomicInteger(0);
//        String userId = UUID.randomUUID().toString();
        String userId = "user_abc";
        
		Runnable task = () -> {
			
            try {
            	// Waiting to start multiple thread at a same time.
            	
            	startLatch.await();
            	RateLimiterResponse rateLimiterResponse = inMemoryRateLimiterService.checkLimit(userId);
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
