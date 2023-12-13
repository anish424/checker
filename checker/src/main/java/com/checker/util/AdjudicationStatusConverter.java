package com.checker.util;

import java.beans.PropertyEditorSupport;

import com.checker.dto.AdjudicationStatus;

public class AdjudicationStatusConverter extends PropertyEditorSupport {

	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
            setValue(AdjudicationStatus.getStatusFromCode(text));
        }
}
