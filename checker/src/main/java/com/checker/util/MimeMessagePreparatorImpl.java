package com.checker.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.javamail.MimeMessagePreparator;

import com.checker.dto.EmailDetailsDto;
import com.checker.exception.EmailException;
import com.checker.service.impl.EmailServiceImpl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MimeMessagePreparatorImpl implements MimeMessagePreparator {

	private Configuration freemarkerMailConfiguration;

	private EmailDetailsDto details;

	private String sender;

	private static final String TEMPLATE_PATH = "templates/email";

	public MimeMessagePreparatorImpl(Configuration freemarkerMailConfiguration, EmailDetailsDto details, String sender,
			StringBuilder br) {
		super();
		this.freemarkerMailConfiguration = freemarkerMailConfiguration;
		this.details = details;
		this.sender = sender;
		this.br = br;
	}

	final StringBuilder br;

	public void prepare(MimeMessage mimeMessage) throws MessagingException, IOException {

		// if email configuration is present in Database, use the same
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(sender));

		InternetAddress inetAddress = new InternetAddress();

		inetAddress.setPersonal(details.getRecipient());
		inetAddress.setAddress(details.getRecipient());

		mimeMessage.setFrom(inetAddress);
		mimeMessage.setSubject(details.getSubject());

		Multipart mp = new MimeMultipart("alternative");

		// Create a "text" Multipart message
		final StringWriter textWriter = createTextMultipartMessage(mp);

		// Create a "HTML" Multipart message
		final StringWriter htmlWriter = createHtmlMultipartMessage(mp, textWriter);

		mimeMessage.setContent(mp);
		br.append(htmlWriter);

	}

	private StringWriter createTextMultipartMessage(Multipart mp) throws IOException, MessagingException {
		BodyPart textPart = new MimeBodyPart();
		freemarkerMailConfiguration.setClassForTemplateLoading(EmailServiceImpl.class, "/");
		Template textTemplate = freemarkerMailConfiguration
				.getTemplate(new StringBuilder(TEMPLATE_PATH).append("/").append(details.getTemplateName()).toString());
		final StringWriter textWriter = new StringWriter();
		try {
			textTemplate.process(details.getTemplateTokens(), textWriter);
		} catch (TemplateException e) {
			log.error("Can't generate text mail", e);
			throw new EmailException("Can't generate text mail", e);
		}
		textPart.setDataHandler(new javax.activation.DataHandler(new DataSourceImpl(textWriter, "text/plain")));
		mp.addBodyPart(textPart);
		return textWriter;
	}

	private StringWriter createHtmlMultipartMessage(Multipart mp, final StringWriter textWriter)
			throws IOException,
			MessagingException {
		Multipart htmlContent = new MimeMultipart("related");
		BodyPart htmlPage = new MimeBodyPart();
		freemarkerMailConfiguration.setClassForTemplateLoading(EmailServiceImpl.class, "/");
		Template htmlTemplate = freemarkerMailConfiguration
				.getTemplate(new StringBuilder(TEMPLATE_PATH).append("/").append(details.getTemplateName()).toString());
		final StringWriter htmlWriter = new StringWriter();
		try {
			htmlTemplate.process(details.getTemplateTokens(), htmlWriter);
		} catch (TemplateException e) {
			log.error("Can't generate HTML mail", e);
			throw new EmailException("Can't generate HTML mail", e);
		}
		htmlPage.setDataHandler(new javax.activation.DataHandler(new DataSourceImpl(textWriter, "text/html")));
		htmlContent.addBodyPart(htmlPage);
		BodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlContent);
		mp.addBodyPart(htmlPart);
		return htmlWriter;
	}

}
