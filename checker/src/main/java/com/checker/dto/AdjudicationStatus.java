package com.checker.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AdjudicationStatus {

	ALL("Engaged,Pre adverse action"), ENGAGED("Engaged"), PRE_ADVERSE_ACTION("Pre adverse action");

	private String value;

	public String getValue() {
		return value;
	}

	private AdjudicationStatus(String value) {
		this.value = value;
	}

	@JsonCreator
	public static AdjudicationStatus getStatusFromCode(String value) {
		for (AdjudicationStatus status : AdjudicationStatus.values()) {
			if (status.getValue().equals(value)) {
				return status;
			}
		}
		return null;
	}
}
