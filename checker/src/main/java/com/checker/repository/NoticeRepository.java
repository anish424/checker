package com.checker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.checker.model.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

}
