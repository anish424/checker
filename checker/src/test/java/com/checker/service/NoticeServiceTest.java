package com.checker.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.model.Notice;
import com.checker.repository.NoticeRepository;
import com.checker.service.impl.NoticeServiceImpl;
import com.checker.util.DateUtil;
import com.checker.util.EntityDtoConvertor;
import com.checker.util.LabelUtils;

class NoticeServiceTest {

	@InjectMocks
	private NoticeServiceImpl service;

	@Mock
	private LabelUtils messages;

	@Mock
	private EmailService emailService;

	@Mock
	private NoticeRepository noticeRepo;

	@Mock
	private EntityDtoConvertor entityDtoConvertor;
	
	@Mock
	private DateUtil dateUtil;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void sendNoticeTest() {
		NoticeDto notice = new NoticeDto();
		notice.setAutoSendDuration(4);
		notice.setBodyEnd("");
		notice.setBodyStart("");
		notice.setNoticeFrom("");
		notice.setNoticeSubject("");
		notice.setNoticeTo("");
		List<ChargeDto> charges = new ArrayList<>();
		ChargeDto dto = new ChargeDto();
		charges.add(dto);
		ChargeDto dto1 = new ChargeDto();
		dto1.setSelected(true);
		charges.add(dto1);
		notice.setCharges(charges);
		when(entityDtoConvertor.convertToEntity(notice)).thenReturn(new Notice());
		service.sendNotice(notice);
		verify(entityDtoConvertor, Mockito.times(1)).convertToEntity(notice);
		
		notice.setCharges(null);
		service.sendNotice(notice);
		verify(entityDtoConvertor, Mockito.times(2)).convertToEntity(notice);
	}

}
