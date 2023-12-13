package com.checker.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.checker.dto.ChargeDto;
import com.checker.dto.EmailDetailsDto;
import com.checker.dto.NoticeDto;
import com.checker.exception.EmailException;
import com.checker.model.Notice;
import com.checker.repository.NoticeRepository;
import com.checker.service.EmailService;
import com.checker.service.NoticeService;
import com.checker.util.DateUtil;
import com.checker.util.EntityDtoConvertor;
import com.checker.util.HtmlConstants;
import com.checker.util.LabelUtils;

@Service
public class NoticeServiceImpl implements NoticeService {

	private static final String EMAIL_FOOTER_COPYRIGHT = "EMAIL_FOOTER_COPYRIGHT";
	private static final String EMAIL_DISCLAIMER = "EMAIL_DISCLAIMER";
	private static final String EMAIL_SPAM_DISCLAIMER = "EMAIL_SPAM_DISCLAIMER";
	private static final String CHARGES_LIST = "CHARGES_LIST";
	private static final String BODY_START = "BODY_START";
	private static final String BODY_END = "BODY_END";
	private static final String TEMPLATE_NAME = "NoticeTemplate.ftl";

	@Value("${reporting.authority}")
	private String reportingAuthority;

	@Autowired
	private LabelUtils messages;

	@Autowired
	private EmailService emailService;

	@Autowired
	private NoticeRepository noticeRepo;

	@Autowired
	private EntityDtoConvertor entityDtoConvertor;
	
	@Autowired
	DateUtil dateUtil;

	public void sendNotice(NoticeDto notice) throws EmailException {
		Locale locale = LocaleContextHolder.getLocale();
		Map<String, String> templateTokens = new HashMap<>();
		String[] adminEmailArg = { notice.getNoticeFrom() };
		String[] copyArg = { reportingAuthority, dateUtil.getPresentYear() };
		String chargeTable = prepareChargeTable(notice);
		populateTemplateMap(notice, locale, templateTokens, adminEmailArg, copyArg, chargeTable);
		String body = emailService.sendNotice(createEmailDetails(notice, templateTokens));

		Notice entity = entityDtoConvertor.convertToEntity(notice);
		entity.setNoticeBody(body);
		entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
		noticeRepo.save(entity);
	}

	private EmailDetailsDto createEmailDetails(NoticeDto notice, Map<String, String> templateTokens) {
		return EmailDetailsDto.builder().recipient(notice.getNoticeTo()).subject(notice.getNoticeSubject())
				.templateTokens(templateTokens).templateName(TEMPLATE_NAME).build();
	}

	private void populateTemplateMap(NoticeDto notice, Locale locale, Map<String, String> templateTokens,
			String[] adminEmailArg, String[] copyArg, String chargeTable) {
		templateTokens.put(EMAIL_FOOTER_COPYRIGHT, messages.getMessage("email.copyright", copyArg, locale));
		templateTokens.put(EMAIL_DISCLAIMER, messages.getMessage("email.disclaimer", adminEmailArg, locale));
		templateTokens.put(EMAIL_SPAM_DISCLAIMER, messages.getMessage("email.spam.disclaimer", locale));
		templateTokens.put(CHARGES_LIST, chargeTable);
		templateTokens.put(BODY_START, notice.getBodyStart());
		templateTokens.put(BODY_END, notice.getBodyEnd());
	}

	private String prepareChargeTable(NoticeDto notice) {
		if (notice.getCharges() == null)
			return "";
		StringBuilder chargeTable = new StringBuilder();
		chargeTable.append(HtmlConstants.UL);
		for (ChargeDto dto : notice.getCharges()) {
			if (dto.isSelected()) {
				chargeTable.append(HtmlConstants.LI).append(dto.getName()).append(HtmlConstants.CLOSING_LI);
			}
		}
		chargeTable.append(HtmlConstants.CLOSING_UL);
		return chargeTable.toString();
	}

}
