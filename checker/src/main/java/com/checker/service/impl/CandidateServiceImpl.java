package com.checker.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checker.dao.CandidateDao;
import com.checker.dto.AdjudicationStatus;
import com.checker.dto.CandidateDto;
import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.exception.NoticeException;
import com.checker.model.Candidate;
import com.checker.model.Charge;
import com.checker.model.Report;
import com.checker.repository.CandidateRepository;
import com.checker.repository.ChargeRepository;
import com.checker.repository.ReportRepository;
import com.checker.service.CandidateService;
import com.checker.service.NoticeService;
import com.checker.util.EntityDtoConvertor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService {

	@Autowired
	private CandidateDao candidateDao;

	@Autowired
	private CandidateRepository candidateRepo;

	@Autowired
	private ReportRepository reportRepo;

	@Autowired
	private EntityDtoConvertor entityDtoConvertor;

	@Autowired
	private ChargeRepository chargeRepo;

	@Autowired
	private NoticeDto notice;

	@Autowired
	private NoticeService noticeService;

	@Override
	public Page<CandidateDto> getCandidates(Pageable pageable, String searchKeyword, List<Status> statusList,
			List<AdjudicationStatus> adjudicationList) throws CandidateException {
		try {
			Page<Candidate> list = candidateDao.getCandidates(pageable, searchKeyword, statusList, adjudicationList);
			List<CandidateDto> dtoList = list.get().map(entry -> entityDtoConvertor.convertToDto(entry, true))
					.collect(Collectors.toList());
			return new PageImpl<>(dtoList, pageable, list.getTotalPages());
		} catch (Exception e) {
			log.error("Failed to get Candidates");
			String errorMSg = StringUtils.isBlank(e.getMessage()) ? "Failed to retrieve list of candidates"
					: e.getMessage();
			throw new CandidateException(errorMSg, e);
		}
	}

	@Override
	public CandidateDto getCandidate(Integer candidateId) throws CandidateException {
		try {
			Candidate enity = candidateDao.getCandidate(candidateId);
			return entityDtoConvertor.convertToDto(enity, false);
		} catch (Exception e) {
			log.error("Failed to get Candidate for candiddate {}", candidateId);
			String errorMSg = StringUtils.isBlank(e.getMessage()) ? "Failed to retrieve candidate" : e.getMessage();
			throw new CandidateException(errorMSg, e);
		}
	}

	public NoticeDto getNotice(Integer candidateId) throws NoticeException {
		try {
			Candidate candidate = candidateDao.getCandidate(candidateId);
			List<Charge> chargeList = chargeRepo.findAll();
			List<ChargeDto> dtoList = chargeList.stream().map(entry -> entityDtoConvertor.convertToDto(entry))
					.collect(Collectors.toList());
			notice.setCharges(dtoList);
			notice.setNoticeTo(candidate.getEmail());
			String bodyStart = notice.getBodyStart().replaceFirst("name", candidate.getName());
			notice.setBodyStart(bodyStart);
			return notice;
		} catch (Exception e) {
			log.error("Failed to create Notice for candiddate {}", candidateId);
			String errorMSg = StringUtils.isBlank(e.getMessage()) ? "Failed to create Notice" : e.getMessage();
			throw new NoticeException(errorMSg, e);
		}
	}

	public void sendNotice(Integer candidateId, List<ChargeDto> charges, Integer autoSendDuration) throws NoticeException {
		try {
			Candidate candidate = candidateDao.getCandidate(candidateId);
			notice.setNoticeTo(candidate.getEmail());
			notice.setAutoSendDuration(autoSendDuration);
			notice.setCharges(charges);
			noticeService.sendNotice(notice);
		} catch (Exception e) {
			log.error("Failed to send notice for candidate: {}", candidateId, e);
			String errorMSg = StringUtils.isBlank(e.getMessage()) ? "Failed to send Notice" : e.getMessage();
			throw new NoticeException(errorMSg, e);
		}
	}

	@Transactional
	public void engage(Integer candidateId) throws CandidateException {
		try {
			Optional<Candidate> candidate = candidateRepo.findById(candidateId);
			Report report = candidate.get().getReport();
			report.setAdjudication(AdjudicationStatus.ENGAGED.getValue());
			reportRepo.saveAndFlush(report);
		} catch (Exception e) {
			log.error("Failed to engage with candidate: {}", candidateId, e);
			String errorMSg = StringUtils.isBlank(e.getMessage()) ? "Failed to engage with candidate" : e.getMessage();
			throw new CandidateException(errorMSg, e);
		}
	}

}
