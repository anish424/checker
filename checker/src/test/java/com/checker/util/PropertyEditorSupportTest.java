package com.checker.util;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.beans.PropertyEditorSupport;

import org.junit.jupiter.api.Test;

import com.checker.dto.AdjudicationStatus;
import com.checker.dto.Status;

class PropertyEditorSupportTest {

	@Test
	void adjudicationStatusConverterTest() {
		PropertyEditorSupport editor = new AdjudicationStatusConverter();
		editor.setAsText("test");
		AdjudicationStatus resource = (AdjudicationStatus) editor.getValue();
		assertNull(resource);

		editor.setAsText(AdjudicationStatus.ALL.getValue());
		resource = (AdjudicationStatus) editor.getValue();
		assertNotNull(resource);

		editor.setAsText(AdjudicationStatus.ENGAGED.getValue());
		resource = (AdjudicationStatus) editor.getValue();
		assertNotNull(resource);

		editor.setAsText(AdjudicationStatus.PRE_ADVERSE_ACTION.getValue());
		resource = (AdjudicationStatus) editor.getValue();
		assertNotNull(resource);
	}

	@Test
	void statusConverterTest() {
		PropertyEditorSupport editor = new StatusConverter();
		editor.setAsText("test");
		Status resource = (Status) editor.getValue();
		assertNull(resource);

		editor = new StatusConverter();
		editor.setAsText(null);
		resource = (Status) editor.getValue();
		assertNull(resource);

		editor.setAsText(Status.ALL_STATUS.getValue());
		resource = (Status) editor.getValue();
		assertNotNull(resource);

		editor.setAsText(Status.CLEAR.getValue());
		resource = (Status) editor.getValue();
		assertNotNull(resource);

		editor.setAsText(Status.CONSIDER.getValue());
		resource = (Status) editor.getValue();
		assertNotNull(resource);
	}

}
