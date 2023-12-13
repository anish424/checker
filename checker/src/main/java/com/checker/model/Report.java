package com.checker.model;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "REPORT")
public class Report {

	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "REPORT_STATUS")
	private String reportStatus;

	@Column(name = "PACKAGE")
	private String reportPackage;

	@Column(name = "REPORT_CREATE_DATE", nullable = false)
	private Timestamp createdDate;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "NOTICE_ID", referencedColumnName = "ID")
	private Notice notice;

	@Column(name = "ADJUDICATION")
	private String adjudication;

	@OneToOne(mappedBy = "report", cascade = CascadeType.ALL)
	private ReportSearchTypeMapper mapper;
	
	@OneToOne(mappedBy = "report")
	private Candidate candidate;
	
}
