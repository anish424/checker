package com.checker.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@PropertySource("classpath:email.properties")
@Configuration
public class EmailConfig {

	@Value("${spring.mail.username}")
	private String emailSender;

	@Value("${spring.mail.password}")
	private String emailPassword;

	@Value("${spring.mail.host}")
	private String emailHost;

	@Value("${spring.mail.port}")
	private Integer emailPort;

	@Value("${spring.mail.transport.protocol}")
	private String emailProtocol;

	@Value("${spring.mail.debug}")
	private String isAuthenticationEnabled;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String isDebugEnabled;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String isConnectionSecured;

	private static final String EMAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	private static final String EMAIL_SMTP_AUTHENTICATION = "mail.smtp.auth";
	private static final String EMAIL_TLS_ENABLE = "mail.smtp.starttls.enable";
	private static final String EMAIL_DEBUG = "mail.debug";

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailHost);
		mailSender.setPort(emailPort);

		mailSender.setUsername(emailSender);
		mailSender.setPassword(emailPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put(EMAIL_TRANSPORT_PROTOCOL, emailProtocol);
		props.put(EMAIL_SMTP_AUTHENTICATION, isAuthenticationEnabled);
		props.put(EMAIL_TLS_ENABLE, isConnectionSecured);
		props.put(EMAIL_DEBUG, isDebugEnabled);

		return mailSender;
	}

}
