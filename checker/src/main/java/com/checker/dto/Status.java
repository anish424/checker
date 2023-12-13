package com.checker.dto;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
	ALL_STATUS("Clear,Consider"), CLEAR("Clear"), CONSIDER("Consider");

	private String value;

	public String getValue() {
		return value;
	}

	private Status(String value) {
		this.value = value;
	}

	@JsonCreator
	public static Status getStatusFromCode(String value) {
		if(StringUtils.isBlank(value))
			return null;
		for (Status status : Status.values()) {
			if (status.getValue().equals(value)) {
				return status;
			}
		}
		return null;
	}
}
