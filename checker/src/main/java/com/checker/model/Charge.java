package com.checker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CHARGE")
public class Charge {

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = "CHARGE_NAME",nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;

}
