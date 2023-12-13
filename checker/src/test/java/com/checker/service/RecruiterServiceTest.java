package com.checker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.checker.dto.SignupDto;
import com.checker.exception.EmailException;
import com.checker.exception.RecruiterException;
import com.checker.model.Recruiter;
import com.checker.repository.RecruiterRepository;
import com.checker.service.impl.RecruiterServiceImpl;
import com.checker.util.EntityDtoConvertor;
import com.checker.util.OtpCache;

class RecruiterServiceTest {

	@InjectMocks
	private RecruiterServiceImpl service;

	@Mock
	private RecruiterRepository recruiterRepo;

	@Mock
	private EntityDtoConvertor entityDtoConvertor;

	@Mock
	private EmailService emailService;
	
	@Mock
	private OtpCache otpCache;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void signupTest() throws RecruiterException {
		SignupDto request = new SignupDto();
		Recruiter entity = new Recruiter();
		when(entityDtoConvertor.convertToEntity(request)).thenReturn(entity);
		service.signup(request);
		verify(recruiterRepo, Mockito.times(1)).save(entity);
	}

	@Test
	void signupExceptionTest() throws RecruiterException {
		SignupDto request = new SignupDto();
		Recruiter entity = new Recruiter();
		when(entityDtoConvertor.convertToEntity(request)).thenReturn(entity);
		service.signup(request);
		doThrow(new NullPointerException("recruiter exception")).when(entityDtoConvertor).convertToEntity(request);
		Throwable thrown = assertThrows(RecruiterException.class, () -> service.signup(request),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("Signup failed, please try again later"));
	}

	@Test
	void sendOtpTest() throws RecruiterException {
		when(emailService.sendSimpleMail(any())).thenReturn(any());
		service.sendOtp("test-user");
		verify(emailService, Mockito.times(1)).sendSimpleMail(any());
	}

	@Test
	void sendOtpExceptionWithMesgTest() throws RecruiterException {
		when(emailService.sendSimpleMail(any())).thenThrow(new EmailException("email exception"));
		Throwable thrown = assertThrows(RecruiterException.class, () -> service.sendOtp("test-user"),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("email exception"));;
	}

	@Test
	void sendOtpExceptionTest() throws RecruiterException {
		when(emailService.sendSimpleMail(any())).thenThrow(EmailException.class);
		Throwable thrown = assertThrows(RecruiterException.class, () -> service.sendOtp("test-user"),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("Failed to send otp"));
	}

	@Test
	void verifyOtpTest() throws RecruiterException {
		when(otpCache.isOtpValid("test-user", "123")).thenReturn(true);
		when(otpCache.isOtpValid("test-user", "1234")).thenReturn(false);
		assertEquals(true, service.verifyOtp("test-user", "123"));
		assertEquals(false, service.verifyOtp("test-user", "1234"));
	}

	@Test
	void verifyNoOtpExceptionTest() throws RecruiterException {
		when(otpCache.isOtpValid("test-user", "123")).thenThrow(new RecruiterException("No otp generated for this user"));
		Throwable thrown = assertThrows(RecruiterException.class, () -> service.verifyOtp("test-user", "123"),
				"Expected doThing() to throw, but it didn't");
		assertTrue(thrown.getMessage().contains("No otp generated for this user"));
	}

}
