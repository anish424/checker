package com.checker.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;

import com.checker.dto.EmailDetailsDto;
import com.checker.exception.EmailException;
import com.checker.service.impl.EmailServiceImpl;
import com.checker.util.DataSourceImpl;
import com.checker.util.MimeMessagePreparatorImpl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

class EmailServiceTest {

	@Mock
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sender;

	@Mock
	private Configuration freemarkerMailConfiguration;

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private EmailServiceImpl service;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void sendSimpleMailTest() {
		EmailDetailsDto emailDetails = getEmailDetails();
		assertEquals("Mail Sent Successfully...", service.sendSimpleMail(emailDetails));
	}

	private EmailDetailsDto getEmailDetails() {
		EmailDetailsDto emailDetails = EmailDetailsDto.builder().recipient("user@test.com").subject("").msgBody("")
				.templateName("").templateTokens(new HashMap<>()).build();
		EmailDetailsDto.builder().toString();
		return emailDetails;
	}

	@Test
	void sendSimpleMailExceptionTest() {
		EmailDetailsDto emailDetails = new EmailDetailsDto();
		emailDetails.setMsgBody("");
		emailDetails.setRecipient("");
		emailDetails.setTemplateName("");
		emailDetails.setSubject("");
		emailDetails.setTemplateTokens(new HashMap<>());
		Throwable thrown = assertThrows(EmailException.class, () -> service.sendSimpleMail(null),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("Can't send email"));
	}

	@Test
	void sendNoticeTest() throws MessagingException, IOException {
		EmailDetailsDto emailDetails = getEmailDetails();
		MimeMessagePreparatorImpl mimeMessagePreparatorImpl = new MimeMessagePreparatorImpl(freemarkerMailConfiguration,
				emailDetails, "user@text.com", new StringBuilder());
		MimeMessage message = mock(MimeMessage.class);
		Template textTemplate = mock(Template.class);
		when(freemarkerMailConfiguration.getTemplate(anyString())).thenReturn(textTemplate);
		mimeMessagePreparatorImpl.prepare(message);

		assertNotNull(service.sendNotice(emailDetails));
	}

	@Test
	void sendNoticeExceptionTest() throws MessagingException, IOException, TemplateException {
		EmailDetailsDto emailDetails = getEmailDetails();

		MimeMessagePreparatorImpl mimeMessagePreparatorImpl = new MimeMessagePreparatorImpl(freemarkerMailConfiguration,
				emailDetails, "user@text.com", new StringBuilder());
		MimeMessage message = mock(MimeMessage.class);
		Template textTemplate = mock(Template.class);
		when(freemarkerMailConfiguration.getTemplate(anyString())).thenReturn(textTemplate);

		doThrow(new TemplateException("exception", null)).when(textTemplate)
				.process(eq(emailDetails.getTemplateTokens()), any(StringWriter.class));

		Throwable thrown1 = assertThrows(EmailException.class, () -> mimeMessagePreparatorImpl.prepare(message),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown1.getMessage().contains("Can't generate text mail"));
	}

	@Test
	void sendNoticeSecondExceptionTest() throws MessagingException, IOException, TemplateException {
		EmailDetailsDto emailDetails = getEmailDetails();

		DataSourceImpl datasource = new DataSourceImpl(new StringWriter(), "text/plain");
		assertNotNull(datasource.getContentType());
		assertNotNull(datasource.getInputStream());
		assertNotNull(datasource.getName());

		Throwable thrown = assertThrows(IOException.class, () -> datasource.getOutputStream(),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("Read-only data"));

		MimeMessagePreparatorImpl mimeMessagePreparatorImpl = new MimeMessagePreparatorImpl(freemarkerMailConfiguration,
				emailDetails, "user@text.com", new StringBuilder());
		MimeMessage message = mock(MimeMessage.class);
		Template textTemplate = mock(Template.class);
		when(freemarkerMailConfiguration.getTemplate(anyString())).thenReturn(textTemplate);

		doNothing().doThrow(new TemplateException("exception", null)).when(textTemplate)
				.process(eq(emailDetails.getTemplateTokens()), any(StringWriter.class));

		Throwable thrown1 = assertThrows(EmailException.class, () -> mimeMessagePreparatorImpl.prepare(message),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown1.getMessage().contains("Can't generate HTML mail"));
	}

}
