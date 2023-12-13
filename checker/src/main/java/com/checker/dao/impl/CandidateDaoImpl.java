package com.checker.dao.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.checker.dao.CandidateDao;
import com.checker.dto.AdjudicationStatus;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.model.Candidate;
import com.checker.model.Report;
import com.checker.repository.CandidateRepository;
import com.checker.util.CheckerConstants;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CandidateDaoImpl implements CandidateDao {

	@Autowired
	EntityManager enityManager;

	@Autowired
	private CandidateRepository candidateRepository;

	@Override
	public Page<Candidate> getCandidates(Pageable pageable, String searchKeyword, List<Status> statusList,
			List<AdjudicationStatus> adjudicationList) throws CandidateException {
		try {
			CriteriaBuilder cb = enityManager.getCriteriaBuilder();
			Long count = getCandidateCount(searchKeyword, statusList, adjudicationList, cb);
			return getCandidateList(pageable, searchKeyword, statusList, adjudicationList, cb, count);
		} catch (Exception e) {
			log.error("failed to get candidate list");
			throw new CandidateException("failed to get candidate list", e);
		}
	}

	private Page<Candidate> getCandidateList(Pageable pageable, String searchKeyword, List<Status> statusList,
			List<AdjudicationStatus> adjudicationList, CriteriaBuilder cb, Long count) {
		CriteriaQuery<Candidate> cq = cb.createQuery(Candidate.class);

		Root<Candidate> root1 = cq.from(Candidate.class);

		addNameLikePredicate(searchKeyword, cb, cq, root1);
		addStatusPredicate(statusList, cb, cq, root1);
		addAdjudicationStatusPredicate(adjudicationList, cb, cq, root1);

		TypedQuery<Candidate> query = enityManager.createQuery(cq);
		query.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
		query.setMaxResults(pageable.getPageSize());

		List<Candidate> list = query.getResultList();
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(),
				pageable.getSort());
		return new PageImpl<>(list, pageRequest, count);
	}

	private Long getCandidateCount(String searchKeyword, List<Status> statusList,
			List<AdjudicationStatus> adjudicationList, CriteriaBuilder cb) {
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Candidate> root = countQuery.from(Candidate.class);

		addNameLikePredicate(searchKeyword, cb, countQuery, root);
		addStatusPredicate(statusList, cb, countQuery, root);
		addAdjudicationStatusPredicate(adjudicationList, cb, countQuery, root);

		countQuery.select(cb.count(root));
		return enityManager.createQuery(countQuery).getSingleResult();
	}

	private <T> void addNameLikePredicate(String searchKeyword, CriteriaBuilder cb, CriteriaQuery<T> query,
			Root<Candidate> root) {
		if (StringUtils.isNotBlank(searchKeyword)) {
			Predicate searchKeyWordPredicate = cb.like(root.get("name"), "%" + searchKeyword + "%");
			query.where(searchKeyWordPredicate);
		}
	}

	private <T> void addAdjudicationStatusPredicate(List<AdjudicationStatus> adjudicationList, CriteriaBuilder cb,
			CriteriaQuery<T> query, Root<Candidate> root) {
		if (adjudicationList != null && !adjudicationList.isEmpty()) {
			Subquery<Report> subquery = query.subquery(Report.class);
			Root<Report> report = subquery.from(Report.class);
			subquery.select(report);
			for (AdjudicationStatus status : adjudicationList) {
				Predicate statusPredicate = null;
				if (status.equals(AdjudicationStatus.ALL)) {
					String[] statusValue = status.getValue().split(",");
					Predicate statusPredicate1 = cb.in(report.get(CheckerConstants.ADJUDICATION))
							.value(Arrays.asList(statusValue));
					Predicate statusPredicate2 = cb.or((report.get(CheckerConstants.ADJUDICATION)).isNull());
					statusPredicate = cb.or(statusPredicate1, statusPredicate2);
				} else {
					statusPredicate = cb.equal(report.get(CheckerConstants.ADJUDICATION), status.toString());
				}
				subquery.where(statusPredicate);
			}
			query.where(cb.in(root.get("report")).value(subquery));
		}
	}

	private <T> void addStatusPredicate(List<Status> statusList, CriteriaBuilder cb, CriteriaQuery<T> query,
			Root<Candidate> root) {
		if (statusList != null && !statusList.isEmpty()) {
			Subquery<Report> subquery = query.subquery(Report.class);
			Root<Report> report = subquery.from(Report.class);
			subquery.select(report);
			for (Status status : statusList) {
				Predicate statusPredicate = null;
				if (status.equals(Status.ALL_STATUS)) {
					String[] statusValue = status.getValue().split(",");
					statusPredicate = cb.in(report.get("reportStatus")).value(Arrays.asList(statusValue));
				} else {
					statusPredicate = cb.equal(report.get("reportStatus"), status.toString());
				}
				subquery.where(statusPredicate);
			}
			query.where(cb.in(root.get("report")).value(subquery));
		}
	}

	public Candidate getCandidate(Integer candidateId) {
		Optional<Candidate> candidate = candidateRepository.findById(candidateId);
		return candidate.isPresent() ? candidate.get() : new Candidate();
	}

	public void setEntityManager(EntityManager entityManager) {
		this.enityManager = entityManager;
	}

	public void setCandidateRepository(CandidateRepository candidateRepository) {
		this.candidateRepository = candidateRepository;
	}

}
