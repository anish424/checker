package com.checker.util;

import java.beans.PropertyEditorSupport;

import com.checker.dto.Status;

public class StatusConverter extends PropertyEditorSupport {

	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
            setValue(Status.getStatusFromCode(text));
        }
}
