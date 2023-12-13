package com.checker.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_EMPTY)
@Setter
@Getter
@NoArgsConstructor
@ToString
public class SearchTypeDto implements Serializable {

	private static final long serialVersionUID = 7543057859651280634L;
	private String name;
	private Status status;
	private String createdDate;

}
