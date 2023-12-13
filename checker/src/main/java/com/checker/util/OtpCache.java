package com.checker.util;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.checker.exception.RecruiterException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OtpCache {

	@Value("${otp.validity}")
	private Long otpValidity;

	private final Map<String, Otp> otpMap = new HashMap<>();

	public boolean isOtpValid(String username, String otp) throws RecruiterException {
		if (getOtp(username).equals(otp)) {
			otpMap.remove(username);
			return true;
		}
		return false;
	}

	private String getOtp(String username) throws RecruiterException {
		Otp otp = otpMap.get(username);
		if (otp == null) {
			log.error("No otp generated for this user {}", username);
			throw new RecruiterException("No otp generated for this user");
		}
		isOtpExpired(otp, username);
		return otp.getValue();
	}

	public String generateOtp(String username) {
		long otp = new SecureRandom().nextInt(900000) + (long) 100000;
		otpMap.put(username, new Otp(String.valueOf(otp), System.currentTimeMillis()));
		return otpMap.get(username).getValue();
	}

	private void isOtpExpired(Otp otp, String username) throws RecruiterException {
		if (System.currentTimeMillis() - otp.getCreatedTime() > otpValidity) {
			otpMap.remove(username);
			log.error("Otp expired for user {}", username);
			throw new RecruiterException("otp expired");
		}
	}
}

@Getter
@AllArgsConstructor
class Otp {
	private String value;
	private long createdTime;
}
