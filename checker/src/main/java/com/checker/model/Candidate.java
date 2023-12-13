package com.checker.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CANDIDATE")
public class Candidate {

	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "CANDIDATE_NAME", nullable = false)
	private String name;

	@Column(name = "CANDIDATE_MOBILE_NO")
	private String mobileNo;

	@Column(name = "CANDIDATe_EMAIL", nullable = false)
	private String email;

	@Column(name = "CANDIDATe_ZIPCODE", nullable = false)
	private String zipcode;

	@Column(name = "CANDIDATe_LOCATION", nullable = false)
	private String location;

	@Column(name = "CANDIDATe_SOCIAL_SECURITY", nullable = false)
	private String socialSecurity;

	@Column(name = "CANDIDATe_DRIVER_LICENSE")
	private String driverLicense;

	@Column(name = "CANDIDATE_DOB", nullable = false)
	private Date dob;

	@Column(name = "CANDIDATE_CREATE_DATE", nullable = false)
	private Timestamp createdDate;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "REPORT_ID", referencedColumnName = "ID")
	private Report report;
	
	@OneToMany(mappedBy = "candidate")
	private Set<Charge> charges = new HashSet<>();

}
