package com.checker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checker.dto.AdjudicationStatus;
import com.checker.dto.CandidateDto;
import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.exception.NoticeException;
import com.checker.service.CandidateService;
import com.checker.util.AdjudicationStatusConverter;
import com.checker.util.StatusConverter;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

	@Autowired
	private CandidateService candidateService;

	@GetMapping
	public Page<CandidateDto> getCandidates(@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false, defaultValue = "1") Integer pageNo,
			@RequestParam(required = false) String searchKeyword, @RequestParam(required = false) List<Status> status,
			@RequestParam(required = false) List<AdjudicationStatus> adjudication) throws CandidateException {

		return candidateService.getCandidates(Pageable.ofSize(pageSize).withPage(pageNo), searchKeyword, status,
				adjudication);
	}

	@GetMapping("/{id}")
	public CandidateDto getCandidate(@PathVariable("id") Integer candidateId) throws CandidateException {
		return candidateService.getCandidate(candidateId);
	}

	@GetMapping("/{id}/notice")
	public NoticeDto getNotice(@PathVariable("id") Integer candidateId) throws NoticeException {
		return candidateService.getNotice(candidateId);
	}

	@PostMapping("/{id}/adverse-action")
	public ResponseEntity<String> adverseAction(@PathVariable("id") Integer candidateId,
			@RequestBody List<ChargeDto> charges, @RequestParam("autoSendDuration") Integer autoSendDuration)
			throws NoticeException {
		candidateService.sendNotice(candidateId, charges, autoSendDuration);
		return ResponseEntity.ok().body("Notice sent successfully");
	}

	@PostMapping("/{id}/engage")
	public ResponseEntity<String> engage(@PathVariable("id") Integer candidateId) throws CandidateException {
		candidateService.engage(candidateId);
		return ResponseEntity.ok().body("Candidate enagged successfully");
	}

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(Status.class, new StatusConverter());
		webdataBinder.registerCustomEditor(AdjudicationStatus.class, new AdjudicationStatusConverter());
	}

}
