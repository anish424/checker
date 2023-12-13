package com.checker.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checker.dao.impl.CandidateDaoImpl;
import com.checker.dto.AdjudicationStatus;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.model.Candidate;
import com.checker.model.Charge;
import com.checker.model.Notice;
import com.checker.model.Report;
import com.checker.model.ReportSearchTypeMapper;
import com.checker.model.SearchType;
import com.checker.repository.CandidateRepository;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class CandidateDaoTest {

	@InjectMocks
	private CandidateDaoImpl dao;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CandidateRepository repo;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
		dao.setEntityManager(entityManager);
		dao.setCandidateRepository(repo);
	}

	@Test
	void getCandidatesTest() throws CandidateException {

		Pageable pageable = createPageable();
		List<Status> statusList = new ArrayList<>();
		statusList.add(Status.ALL_STATUS);
		statusList.add(Status.CLEAR);
		List<AdjudicationStatus> adjudicationList = new ArrayList<>();
		adjudicationList.add(AdjudicationStatus.ALL);
		adjudicationList.add(AdjudicationStatus.ENGAGED);
		Page<Candidate> list = dao.getCandidates(pageable, "test", statusList, adjudicationList);
		assertNotNull(list);
		list = dao.getCandidates(pageable, "", null, null);
		assertNotNull(list);
		list = dao.getCandidates(pageable, "", new ArrayList<>(), new ArrayList<>());
		assertNotNull(list);
	}

	@Test
	void getCandidatesExceptionTest() throws CandidateException {

		dao.setEntityManager(null);
		Pageable pageable = createPageable();
		List<Status> statusList = new ArrayList<>();
		statusList.add(Status.ALL_STATUS);
		statusList.add(Status.CLEAR);
		List<AdjudicationStatus> adjudicationList = new ArrayList<>();
		adjudicationList.add(AdjudicationStatus.ALL);
		adjudicationList.add(AdjudicationStatus.ENGAGED);
		Throwable thrown = assertThrows(CandidateException.class,
				() -> dao.getCandidates(pageable, "test", statusList, adjudicationList),
				"Expected doThing() to throw, but it didn't");

		assertTrue(thrown.getMessage().contains("failed to get candidate list"));
	}

	@Test
	void getCandidateTest() throws ParseException {
		Candidate candidate = repo.save(getCandidate());
		candidate = dao.getCandidate(0);
		assertNotNull(candidate);
		
		repo.deleteAll();
		candidate = dao.getCandidate(1);
		assertNull(candidate.getName());
	}

	private Pageable createPageable() {
		int pageSize = 10;
		int pageNo = 1;
		Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNo);
		return pageable;
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
		// report.setNotice(notice);
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
