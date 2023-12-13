package com.checker.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.AuthenticationException;

import com.checker.configuration.JwtAuthenticationEntryPoint;

class JwtAuthenticationEntryPointTest {

	@InjectMocks
	private JwtAuthenticationEntryPoint entryPoint;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void commenceTest() throws IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpServletRequest request = new MockHttpServletRequest();
		AuthenticationException authException = mock(AuthenticationException.class);
		entryPoint.commence(request, response, authException);
		verify(response, Mockito.times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}

}
