package com.checker.service;

import com.checker.dto.SignupDto;
import com.checker.exception.RecruiterException;

public interface RecruiterService {
	
	public void signup(SignupDto request) throws RecruiterException;
	public boolean verifyOtp(String username, String otp) throws RecruiterException;
	public void sendOtp(String username) throws RecruiterException;
}
