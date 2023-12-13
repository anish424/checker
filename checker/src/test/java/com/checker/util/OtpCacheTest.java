package com.checker.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.checker.exception.RecruiterException;

class OtpCacheTest {

	@InjectMocks
	private OtpCache otpCache;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(otpCache, "otpValidity", 24000l);
	}

	@Test
	void generateOtpTest() {
		assertNotNull(otpCache.generateOtp("test_user"));
	}

	@Test
	void verifyOtpTest() throws RecruiterException {
		String otp = otpCache.generateOtp("test_user");
		assertEquals(false, otpCache.isOtpValid("test_user", "123"));
		assertEquals(true, otpCache.isOtpValid("test_user", otp));
	}

	@Test
	void verifyOtpExceptionTest() throws RecruiterException {
		String otp = otpCache.generateOtp("test_user");
		Throwable thrown = assertThrows(RecruiterException.class, () -> otpCache.isOtpValid("test_user1", otp),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("No otp generated for this user"));

		ReflectionTestUtils.setField(otpCache, "otpValidity", -1l);
		Throwable thrown1 = assertThrows(RecruiterException.class, () -> otpCache.isOtpValid("test_user", otp),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown1.getMessage().contains("otp expired"));
	}

}
