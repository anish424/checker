package com.checker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checker.model.Charge;

public interface ChargeRepository  extends JpaRepository<Charge, Integer> {

}
