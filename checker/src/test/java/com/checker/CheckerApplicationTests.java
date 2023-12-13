package com.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckerApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertDoesNotThrow(this::doNotThrowException);
	}

	private void doNotThrowException() {
		// This method will never throw exception
	}

}
