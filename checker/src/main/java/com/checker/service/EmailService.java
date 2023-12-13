package com.checker.service;

import com.checker.dto.EmailDetailsDto;
import com.checker.exception.EmailException;

public interface EmailService {

	// Method
	// To send a simple email
	String sendSimpleMail(EmailDetailsDto details) throws EmailException;
	String sendNotice(EmailDetailsDto details) throws EmailException;

}