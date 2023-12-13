package com.checker.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.checker.configuration.JwtTokenUtil;

class JwtTokenUtilTest {

	@InjectMocks
	JwtTokenUtil jwtTokenUtil;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(jwtTokenUtil, "jwtSecret", "hello");
		ReflectionTestUtils.setField(jwtTokenUtil, "jwtTokenValidity", 18000);
	}

	@Test
	void validateTokentest() {
		UserDetails userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn("test_user");
		String token = jwtTokenUtil.generateToken(userDetails);
		assertNotNull(token);
		assertEquals("test_user", jwtTokenUtil.getUsernameFromToken(token));
		assertNotNull(jwtTokenUtil.getExpirationDateFromToken(token));
		assertEquals(Boolean.TRUE, jwtTokenUtil.validateToken(token, userDetails));
		UserDetails userDetails1 = mock(UserDetails.class);
		when(userDetails1.getUsername()).thenReturn("test_user1");
		assertEquals(Boolean.FALSE, jwtTokenUtil.validateToken(token, userDetails1));
	}
}
