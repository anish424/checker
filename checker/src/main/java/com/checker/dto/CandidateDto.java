package com.checker.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_EMPTY)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class CandidateDto implements Serializable {

	private static final long serialVersionUID = -5930026940602091300L;

	private Integer id;
	private String name;
	private AdjudicationStatus adjudicationStatus;
	private Status status;
	private String location;
	private String firstSearchCompletionDate;
	private String email;
	private String dob;
	private String mobileNo;
	private String zipcode;
	private String socialSecurity;
	private String driverLicense;
	private Timestamp createdDate;
	
	private String employeePackage;
	private String reportCreatedDate;
	private String reportCompletedDate;
	private List<SearchTypeDto> searchtypeList;
}
