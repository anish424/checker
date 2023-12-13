package com.checker.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.checker.dto.AdjudicationStatus;
import com.checker.dto.CandidateDto;
import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.exception.NoticeException;

public interface CandidateService {

	public Page<CandidateDto> getCandidates(Pageable pageable, String searchKeyword, List<Status> status,
			List<AdjudicationStatus> adjudication) throws CandidateException;
	
	public CandidateDto getCandidate(Integer candidateId) throws CandidateException;
	public NoticeDto getNotice(Integer candidateId) throws NoticeException;
	public void sendNotice(Integer candidateId, List<ChargeDto> charges, Integer autoSendDuration) throws NoticeException;
	public void engage(Integer candidateId) throws CandidateException;

}
