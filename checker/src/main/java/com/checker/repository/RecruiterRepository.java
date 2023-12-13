package com.checker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checker.model.Recruiter;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

	Recruiter findByUsername(String username);
}
