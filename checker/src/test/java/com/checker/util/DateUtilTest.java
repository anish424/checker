package com.checker.util;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class DateUtilTest {

	@InjectMocks
	private DateUtil dateUtil;
	
	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void getPresentYearTest() {
		assertNotNull(dateUtil.getPresentYear());
	}

}
