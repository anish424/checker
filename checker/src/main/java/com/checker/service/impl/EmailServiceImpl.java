package com.checker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.checker.dto.EmailDetailsDto;
import com.checker.exception.EmailException;
import com.checker.service.EmailService;
import com.checker.util.MimeMessagePreparatorImpl;

import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;

// Annotation
@Service
// Class
// Implementing EmailService interface
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sender;

	@Autowired
	private Configuration freemarkerMailConfiguration;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public String sendNotice(EmailDetailsDto details) throws EmailException {

		final StringBuilder br = new StringBuilder();

		MimeMessagePreparator preparator = new MimeMessagePreparatorImpl(freemarkerMailConfiguration, details, sender, br);
		mailSender.send(preparator);
		return br.toString();
	}

	// Method 1
	// To send a simple email
	@Async
	public String sendSimpleMail(EmailDetailsDto details) throws EmailException {

		// Try block to check for exceptions
		try {
			// Creating a simple mail message
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			// Setting up necessary details
			mailMessage.setFrom(sender);
			mailMessage.setTo(details.getRecipient());
			mailMessage.setText(details.getMsgBody());
			mailMessage.setSubject(details.getSubject());

			// Sending the mail
			javaMailSender.send(mailMessage);
			return "Mail Sent Successfully...";
		}

		// Catch block to handle the exceptions
		catch (Exception e) {
			log.error("Can't send email", e);
			throw new EmailException("Can't send email", e);
		}
	}

}
