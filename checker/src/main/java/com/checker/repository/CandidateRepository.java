package com.checker.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.checker.model.Candidate;

@Repository
public interface CandidateRepository extends PagingAndSortingRepository<Candidate, Integer> {
}
