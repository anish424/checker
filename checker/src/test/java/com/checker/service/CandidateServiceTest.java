package com.checker.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.checker.dao.CandidateDao;
import com.checker.dto.AdjudicationStatus;
import com.checker.dto.CandidateDto;
import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.exception.EmailException;
import com.checker.exception.NoticeException;
import com.checker.model.Candidate;
import com.checker.model.Charge;
import com.checker.model.Report;
import com.checker.repository.CandidateRepository;
import com.checker.repository.ChargeRepository;
import com.checker.repository.ReportRepository;
import com.checker.service.impl.CandidateServiceImpl;
import com.checker.util.EntityDtoConvertor;

class CandidateServiceTest {

	@InjectMocks
	private CandidateServiceImpl service;

	@Mock
	private CandidateRepository candidateRepo;

	@Mock
	private CandidateDao candidateDao;

	@Mock
	private EntityDtoConvertor entityDtoConvertor;

	@Mock
	private ChargeRepository chargeRepo;

	@Mock
	private NoticeService noticeService;

	@Mock
	private NoticeDto notice;

	@Mock
	private ReportRepository reportRepo;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void getCandidatesTest() throws CandidateException {
		Pageable pageable = createPageable();
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(),
				pageable.getSort());
		Candidate candidate = new Candidate();
		List<Candidate> list = createCandidateList(candidate);
		Page<Candidate> page = new PageImpl<>(list, pageRequest, 10);
		List<Status> statusList = new ArrayList<>();
		List<AdjudicationStatus> adjudicationList = new ArrayList<>();
		when(candidateDao.getCandidates(pageable, "", statusList, adjudicationList)).thenReturn(page);
		when(entityDtoConvertor.convertToDto(candidate, Boolean.TRUE)).thenReturn(new CandidateDto());
		Page<CandidateDto> dto = service.getCandidates(pageable, "", new ArrayList<>(), new ArrayList<>());
		Assertions.assertNotNull(dto);

	}

	private List<Candidate> createCandidateList(Candidate candidate) {
		List<Candidate> list = new ArrayList<>();
		Set<Charge> charges = createChargesList();
		candidate.setCharges(charges);
		list.add(candidate);
		return list;
	}

	private Set<Charge> createChargesList() {
		Set<Charge> charges = new HashSet<>();
		Charge charge = new Charge();
		charge.setId(1);
		charge.setName("");
		charges.add(charge);
		return charges;
	}

	private Pageable createPageable() {
		int pageSize = 10;
		int pageNo = 1;
		Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNo);
		return pageable;
	}

	@Test
	void getCandidatesExceptionTest() throws CandidateException {
		Pageable pageable = createPageable();
		List<Status> statusList = new ArrayList<>();
		List<AdjudicationStatus> adjudicationList = new ArrayList<>();
		when(candidateDao.getCandidates(pageable, "", statusList, adjudicationList))
				.thenThrow(new CandidateException("candidate exception"));
		Throwable thrown = assertThrows(CandidateException.class,
				() -> service.getCandidates(pageable, "", new ArrayList<>(), new ArrayList<>()),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("candidate exception"));
	}

	@Test
	void getCandidatesNullPointerExceptionTest() throws CandidateException {
		Pageable pageable = createPageable();
		List<Status> statusList = new ArrayList<>();
		List<AdjudicationStatus> adjudicationList = new ArrayList<>();
		when(candidateDao.getCandidates(pageable, "", statusList, adjudicationList))
				.thenThrow(NullPointerException.class);
		Throwable thrown = assertThrows(CandidateException.class,
				() -> service.getCandidates(pageable, "", new ArrayList<>(), new ArrayList<>()),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("Failed to retrieve list of candidates"));
	}

	@Test
	void getCandidateTest() throws CandidateException {
		Candidate candidate = new Candidate();
		when(candidateDao.getCandidate(1)).thenReturn(candidate);
		when(entityDtoConvertor.convertToDto(candidate, Boolean.FALSE)).thenReturn(new CandidateDto());
		CandidateDto dto = service.getCandidate(1);
		Assertions.assertNotNull(dto);
	}

	@Test
	void getCandidateExceptionWithMessageTest() throws CandidateException {
		Candidate candidate = new Candidate();
		when(candidateDao.getCandidate(1)).thenThrow(new NullPointerException("candidate exception"));
		when(entityDtoConvertor.convertToDto(candidate, Boolean.FALSE)).thenReturn(new CandidateDto());
		Throwable thrown = assertThrows(CandidateException.class, () -> service.getCandidate(1),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("candidate exception"));
	}

	@Test
	void getCandidateExceptionTest() throws CandidateException {
		Candidate candidate = new Candidate();
		when(candidateDao.getCandidate(1)).thenThrow(NullPointerException.class);
		when(entityDtoConvertor.convertToDto(candidate, Boolean.FALSE)).thenReturn(new CandidateDto());
		Throwable thrown = assertThrows(CandidateException.class, () -> service.getCandidate(1),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("Failed to retrieve candidate"));
	}

	@Test
	void getNoticeTest() throws NoticeException {
		Candidate candidate = new Candidate();
		candidate.setName("");
		when(candidateDao.getCandidate(1)).thenReturn(candidate);
		List<Charge> charges = new ArrayList<>(createChargesList());
		when(chargeRepo.findAll()).thenReturn(charges);
		when(entityDtoConvertor.convertToDto(charges.get(0))).thenReturn(new ChargeDto());
		when(notice.getBodyStart()).thenReturn("name");
		NoticeDto dto = service.getNotice(1);
		Assertions.assertNotNull(dto);
	}

	@Test
	void getNoticeExceptionWithMessageTest() throws NoticeException {
		when(candidateDao.getCandidate(1)).thenThrow(new NullPointerException("notice exception"));
		Throwable thrown = assertThrows(NoticeException.class, () -> service.getNotice(1),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("notice exception"));
	}

	@Test
	void getNoticeExceptionTest() throws NoticeException {
		when(candidateDao.getCandidate(1)).thenThrow(NullPointerException.class);
		Throwable thrown = assertThrows(NoticeException.class, () -> service.getNotice(1),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("Failed to create Notice"));
	}

	@Test
	void sendNoticeTest() throws Exception {
		when(candidateDao.getCandidate(any())).thenReturn(new Candidate());
		doNothing().when(noticeService).sendNotice(notice);
		service.sendNotice(1, new ArrayList<>(), 1);
		verify(noticeService, Mockito.times(1)).sendNotice(notice);
	}
	
	@Test
	void sendNoticeExceptionWithMessageTest() throws Exception {
		when(candidateDao.getCandidate(any())).thenReturn(new Candidate());
		doThrow(new EmailException("notice exception")).when(noticeService).sendNotice(notice);
		Throwable thrown = assertThrows(NoticeException.class, () -> service.sendNotice(1, new ArrayList<>(), 1),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("notice exception"));

	}

	@Test
	void sendNoticeExceptionTest() throws Exception {
		doThrow(EmailException.class).when(noticeService).sendNotice(notice);
		Throwable thrown = assertThrows(NoticeException.class, () -> service.sendNotice(1, new ArrayList<>(), 1),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("Failed to send Notice"));
	}

	@Test
	void engageTest() throws Exception {
		Candidate candidate = new Candidate();
		Report report = new Report();
		candidate.setReport(report);
		Optional<Candidate> optional = Optional.of(candidate);
		when(candidateRepo.findById(1)).thenReturn(optional);
		service.engage(1);
		verify(candidateRepo, Mockito.times(1)).findById(1);
	}

	@Test
	void engageExceptionWithMessageTest() throws Exception {
		doThrow(new NullPointerException("candidate exception")).when(candidateRepo).findById(1);
		Throwable thrown = assertThrows(CandidateException.class, () -> service.engage(1),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("candidate exception"));
	}

	@Test
	void engageExceptionTest() throws Exception {
		doThrow(NullPointerException.class).when(candidateRepo).findById(1);
		Throwable thrown = assertThrows(CandidateException.class, () -> service.engage(1),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("Failed to engage with candidate"));
	}

}
