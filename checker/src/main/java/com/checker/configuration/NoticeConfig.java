package com.checker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.checker.dto.NoticeDto;
import com.checker.util.HtmlConstants;

@Configuration
@PropertySource("classpath:notice.properties")
public class NoticeConfig {

	@Value("${notice.subject}")
	private String subject;

	@Value("${notice.firstLine}")
	private String noticeStart;

	@Value("${notice.body.line.first}")
	private String bodyFirstLine;

	@Value("${notice.body.line.second}")
	private String bodySecondLine;

	@Value("${notice.body.line.third}")
	private String bodyThirdLine;

	@Value("${notice.body.adverse.action.header}")
	private String adverseActionHeader;

	@Value("${notice.body.adverse.action.note.line.first}")
	private String adverseActionStart;

	@Value("${notice.body.adverse.action.note.line.second}")
	private String adverseActionEnd;

	@Value("${notice.body.regards.line.first}")
	private String regardsStart;

	@Value("${notice.body.regards.line.second}")
	private String regardsEnd;

	@Value("${spring.mail.username}")
	private String sender;

	@Bean
	@Scope("prototype")
	public NoticeDto notice() {
		StringBuilder bodyStart = new StringBuilder();
		bodyStart.append(noticeStart);
		bodyStart.append(HtmlConstants.LINE_BREAK);
		bodyStart.append(bodyFirstLine);
		bodyStart.append(HtmlConstants.LINE_BREAK);
		bodyStart.append(bodySecondLine);
		bodyStart.append(HtmlConstants.LINE_BREAK);
		bodyStart.append(bodyThirdLine);

		bodyStart.append(HtmlConstants.LINE_BREAK);
		bodyStart.append(HtmlConstants.LINE_BREAK);
		bodyStart.append(HtmlConstants.BOLD);
		bodyStart.append(adverseActionHeader);
		bodyStart.append(HtmlConstants.CLOSING_BOLD);
		bodyStart.append(HtmlConstants.LINE_BREAK);

		StringBuilder bodyEnd = new StringBuilder();
		bodyEnd.append(adverseActionStart);
		bodyEnd.append(HtmlConstants.LINE_BREAK);
		bodyEnd.append(adverseActionEnd);
		bodyEnd.append(HtmlConstants.LINE_BREAK);
		bodyEnd.append(HtmlConstants.LINE_BREAK);
		bodyEnd.append(regardsStart);
		bodyEnd.append(HtmlConstants.LINE_BREAK);
		bodyEnd.append(regardsEnd);
		return NoticeDto.builder().noticeSubject(subject).bodyStart(bodyStart.toString()).bodyEnd(bodyEnd.toString())
				.noticeFrom(sender).build();
	}

}
