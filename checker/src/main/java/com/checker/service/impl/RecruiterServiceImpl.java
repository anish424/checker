package com.checker.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checker.dto.EmailDetailsDto;
import com.checker.dto.SignupDto;
import com.checker.exception.RecruiterException;
import com.checker.model.Recruiter;
import com.checker.repository.RecruiterRepository;
import com.checker.service.EmailService;
import com.checker.service.RecruiterService;
import com.checker.util.EntityDtoConvertor;
import com.checker.util.OtpCache;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecruiterServiceImpl implements RecruiterService {

	@Autowired
	private RecruiterRepository recruiterRepo;

	@Autowired
	private EntityDtoConvertor entityDtoConvertor;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private OtpCache otpCache;

	public void signup(SignupDto request) throws RecruiterException {
		try {
			Recruiter entity = entityDtoConvertor.convertToEntity(request);
			recruiterRepo.save(entity);
		} catch (Exception e) {
			log.error("Signup failed, please try again later for {}", request.getUsername(), e);
			throw new RecruiterException("Signup failed, please try again later", e);
		}
	}

	public void sendOtp(String username) throws RecruiterException {
		try {
			String otp = otpCache.generateOtp(username);
			EmailDetailsDto details = new EmailDetailsDto();
			details.setMsgBody("Otp: " + otp + " ,valid for 4 minutes");
			details.setRecipient(username);
			details.setSubject("One Time Password (OTP) for your login");
			emailService.sendSimpleMail(details);
		} catch (Exception e) {
			log.error("Failed to send otp for {}", username, e);
			String errorMSg = StringUtils.isBlank(e.getMessage()) ? "Failed to send otp" : e.getMessage();
			throw new RecruiterException(errorMSg, e);
		}
	}

	public boolean verifyOtp(String username, String otp) throws RecruiterException {
		try {
			return otpCache.isOtpValid(username,otp);
		} catch (Exception e) {
			log.error("Failed to verify otp for {}", username, e);
			throw new RecruiterException(e.getMessage(), e);
		}
	}

}
