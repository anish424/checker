package com.checker.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.checker.dto.AdjudicationStatus;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.model.Candidate;

public interface CandidateDao {

	Page<Candidate> getCandidates(Pageable pageable, String searchKeyword, List<Status> status,
			List<AdjudicationStatus> adjudication) throws CandidateException;
	
	public Candidate getCandidate(Integer candidateId);
}
