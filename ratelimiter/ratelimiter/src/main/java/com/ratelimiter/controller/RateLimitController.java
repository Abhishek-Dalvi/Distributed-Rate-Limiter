package com.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ratelimiter.model.RateLimiterRequest;
import com.ratelimiter.model.RateLimiterResponse;
import com.ratelimiter.service.RateLimiterService;

@RestController
@RequestMapping("/api")
public class RateLimitController {
	private final RateLimiterService rateLimiterService;

	/**
	 * @param rateLimiterService
	 */
	public RateLimitController(RateLimiterService rateLimiterService) {
		super();
		this.rateLimiterService = rateLimiterService;
	}
	
	@PostMapping("/request")
	public RateLimiterResponse rateLimiterResponse(@RequestBody RateLimiterRequest request) {
		return rateLimiterService.checkLimit(request.getUserId());
		
	}
	
	@GetMapping("/hello")
	public String helloReturn() {
		return "Hello";
	}
}
