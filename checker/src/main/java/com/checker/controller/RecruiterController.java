package com.checker.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checker.configuration.JwtTokenUtil;
import com.checker.dto.JwtResponse;
import com.checker.dto.SignupDto;
import com.checker.exception.RecruiterException;
import com.checker.service.JwtUserDetailsService;
import com.checker.service.RecruiterService;

@RestController
public class RecruiterController {

	@Autowired
	private RecruiterService recruiterService;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody @Valid SignupDto request) throws RecruiterException {
		recruiterService.signup(request);
		return ResponseEntity.ok().body("Signup successful");
	}

	@PostMapping("/forget-password")
	public ResponseEntity<String> sendOtp(@RequestParam String username) throws RecruiterException {
		recruiterService.sendOtp(username);
		return ResponseEntity.ok().body("Otp sent successfully");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<Object> verifyotp(@RequestParam String otp, @RequestParam String username) throws RecruiterException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (recruiterService.verifyOtp(userDetails.getUsername(), otp)) {
			final String token = jwtTokenUtil.generateToken(userDetails);
			return ResponseEntity.ok(new JwtResponse(token));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inavlid Otp");
		}
	}
}
