package com.checker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.checker.exception.JwtException;
import com.checker.model.Recruiter;
import com.checker.repository.RecruiterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private RecruiterRepository recruiterRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Recruiter user = recruiterRepository.findByUsername(username);

		if (user != null) {
			return user;
		} else {
			log.error("User not found with username: {}", username);
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

	public void authenticate(String username, String password) throws JwtException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			log.error("User is marked inactive: {}", username, e);
			throw new JwtException("User is marked inactive", e);
		} catch (BadCredentialsException e) {
			log.error("Invalid credentials: {}", username, e);
			throw new JwtException("Invalid credentials", e);
		}
	}

}
