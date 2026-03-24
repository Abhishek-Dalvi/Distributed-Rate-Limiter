package com.ratelimiter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ratelimiter.service.RateLimiterService;


@WebMvcTest
public class RatelimiterTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private RateLimiterService rateLimiterService;
	
	@Test
	void rateLimitTest() throws Exception {
		mockMvc.perform(get("/api/hello")).andExpect(status().isOk()).andExpect( content().string("Hello"));

	}
	
	@Test
	void  rateLimiterTest() throws Exception {
		mockMvc.perform(post("/api/request").content("{\"userId\":\"123\"}").contentType("application/json")).andExpect(status().isOk());
	}

}
