package com.checker.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checker.config.TestConfig;
import com.checker.dto.AdjudicationStatus;
import com.checker.dto.CandidateDto;
import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.dto.SignupDto;
import com.checker.dto.Status;
import com.checker.model.Candidate;
import com.checker.model.Charge;
import com.checker.model.Notice;
import com.checker.model.Recruiter;
import com.checker.model.Report;
import com.checker.model.ReportSearchTypeMapper;
import com.checker.model.SearchType;

@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
class EntityDtoConvertorTest {

	@Autowired
	private EntityDtoConvertor convertor;

	@Test
	void convertToCandidateDtoTest() throws ParseException {
		Candidate entry = getCandidate();
		CandidateDto dto = convertor.convertToDto(entry, Boolean.FALSE);
		assertNotNull(dto.toString());

		dto = convertor.convertToDto(entry, Boolean.TRUE);
		assertNotNull(dto.toString());
		
		entry.setReport(null);
		dto = convertor.convertToDto(entry, Boolean.TRUE);
		assertNotNull(dto.toString());
	}

	@Test
	void convertToCandidateDtoExceptionTest() throws ParseException {
		Candidate entry = getCandidate();
		Set<SearchType> types = entry.getReport().getMapper().getSearchTypes();
		List<SearchType> list = new ArrayList<>(types);
		list.get(0).setCreatedDate("13-40-2023");
		CandidateDto dto = convertor.convertToDto(entry, Boolean.FALSE);
		assertNotNull(dto.toString());

		StringToDateConverter dateConvertor = new StringToDateConverter();
		MappingContext<String, java.util.Date> mappingContext = mock(MappingContext.class);
		when(mappingContext.getSource()).thenReturn("12/12/2023");
		assertNotNull(dateConvertor.convert(mappingContext));

		MappingContext<String, java.util.Date> mappingContext1 = mock(MappingContext.class);
		when(mappingContext1.getSource()).thenReturn("12-12-2023");
		assertNull(dateConvertor.convert(mappingContext1));
	}

	@Test
	void convertToChargeTest() {
		Charge charge = getCharge();
		ChargeDto chargeDto = convertor.convertToDto(charge);
		assertNotNull(chargeDto.toString());

		Charge entity = convertor.convertToEntity(chargeDto);
		assertEquals(charge.getName(), entity.getName());
	}

	@Test
	void convertToNoticeTest() {
		NoticeDto noticeDto = getNoticeDto();
		Notice noticeEntity = convertor.convertToEntity(noticeDto);
		assertNotNull(noticeEntity.toString());
	}

	@Test
	void convertToRecruiterEntity() {
		SignupDto request = new SignupDto();
		request.setName("name");
		request.setPassword("username");
		request.setUsername("password");
		Recruiter user = convertor.convertToEntity(request);
		user.setId(1);
		user.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
		user.toString();
		user.isAccountNonExpired();
		user.isAccountNonLocked();
		user.isActive();
		user.isCredentialsNonExpired();
		user.isEnabled();
		user.getAuthorities();
		assertNotNull(user);
	}

	private NoticeDto getNoticeDto() {
		List<ChargeDto> dtoList = new ArrayList<>();
		dtoList.add(convertor.convertToDto(getCharge()));
		NoticeDto dto = NoticeDto.builder().autoSendDuration(1).bodyStart("").bodyEnd("").noticeFrom("").noticeTo("")
				.charges(dtoList).build();
		assertNotNull(NoticeDto.builder().noticeSubject("").bodyStart("").bodyEnd("").noticeFrom("").noticeTo("")
				.charges(dtoList).toString());

		return dto;
	}

	private Candidate getCandidate() throws ParseException {
		Candidate entry = new Candidate();
		List<Charge> charges = new ArrayList<>();
		Charge charge = getCharge();
		charges.add(charge);
		charge.setCandidate(entry);
		entry.setCharges(new HashSet<>(charges));
		entry.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		entry.setDob(new Date(System.currentTimeMillis()));
		entry.setDriverLicense("1234");
		charge.getCandidate();
		entry.setEmail("user@test.com");
		entry.setId(1);
		entry.setLocation("location");
		entry.setMobileNo("1234567890");
		entry.setName("test");
		Report report = new Report();
		report.setAdjudication(AdjudicationStatus.ENGAGED.getValue());
		report.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		report.setId(1);
		ReportSearchTypeMapper mapper = new ReportSearchTypeMapper();
		mapper.setId(1);
		List<SearchType> types = new ArrayList<>();
		SearchType type = new SearchType();
		type.setCreatedDate("12/12/2023");
		type.setId(1l);
		report.setCandidate(entry);
		type.setName("name");
		type.setStatus(Status.CONSIDER.getValue());
		types.add(type);
		mapper.getReport();
		SearchType type1 = new SearchType();
		type1.setCreatedDate("11/11/2023");
		report.getCandidate();
		report.getId();
		type1.setId(2l);
		report.getNotice();
		type1.setName("name");
		type1.setStatus(Status.CONSIDER.getValue());
		types.add(type1);

		mapper.setSearchTypes(new HashSet<>(types));
		report.setMapper(mapper);
		Notice notice = new Notice();
		notice.setAutoSendDuration(10);
		notice.setCreateDate(new Timestamp(System.currentTimeMillis()));
		notice.setId(1);
		notice.setNoticeBody("body");
		notice.setNoticeSubject("subject");
		notice.setNoticeTo("to");
		report.setNotice(notice);
		type.toString();
		report.setReportPackage("package");
		report.setReportStatus(Status.CLEAR.getValue());
		entry.getCharges();
		mapper.getId();
		mapper.getSearchTypes();
		mapper.setReport(report);
		entry.setReport(report);
		entry.setSocialSecurity("123-123-2345");
		JSONObject data = new JSONObject();
		data.put("id", type.getId());
		data.put("name", type.getName());
		data.put("status", type.getStatus());
		data.put("createdDate", type.getCreatedDate());
		new SearchType(data);
		notice.toString();
		entry.setZipcode("123456");

		return entry;
	}

	private Charge getCharge() {
		Charge charge = new Charge();
		charge.setId(1);
		charge.setName("name");
		return charge;
	}

}
