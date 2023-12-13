package com.checker.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.checker.exception.JwtException;
import com.checker.model.Recruiter;
import com.checker.repository.RecruiterRepository;

class JwtUserDetailsServiceTest {

	@InjectMocks
	private JwtUserDetailsService service;

	@Mock
	private RecruiterRepository recruiterRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void loadUserByUsernameTest() {
		Recruiter user = new Recruiter();
		user.setUsername("test_user");
		when(recruiterRepository.findByUsername("test_user")).thenReturn(user);
		assertNotNull(service.loadUserByUsername("test_user"));
	}

	@Test
	void loadUserByUsernameFailedest() {
		Recruiter user = new Recruiter();
		user.setUsername("test_user");
		when(recruiterRepository.findByUsername("test_user1")).thenReturn(null);
		Throwable thrown = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test_user1"),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("User not found with username: "));
	}

	@Test
	void authenticateTest() throws Exception {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(any(Authentication.class));
		service.authenticate("test_user","");
		verify(authenticationManager, Mockito.times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	void authenticateExceptionTest() throws Exception {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new DisabledException(""));
		Throwable thrown = assertThrows(JwtException.class, () -> service.authenticate("test_user",""),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("User is marked inactive"));
	}

	@Test
	void authenticateBadCredentialsExceptionTest() throws Exception {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(""));
		Throwable thrown = assertThrows(JwtException.class, () -> service.authenticate("test_user",""),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("Invalid credentials"));
	}

}
