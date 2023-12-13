package com.checker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.checker.configuration.JwtAuthenticationEntryPoint;
import com.checker.configuration.JwtRequestFilter;
import com.checker.configuration.JwtTokenUtil;
import com.checker.dto.SignupDto;
import com.checker.exception.RecruiterException;
import com.checker.exception.RestResponseEntityExceptionHandler;
import com.checker.service.JwtUserDetailsService;
import com.checker.service.RecruiterService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RecruiterController.class)
@TestMethodOrder(OrderAnnotation.class)
class RecruiterControllerTest {

	@InjectMocks
	RecruiterController controller;

	@MockBean
	JwtUserDetailsService jwtUserDetailsService;

	@MockBean
	JwtTokenUtil JwtTokenUtil;

	@MockBean
	JwtAuthenticationEntryPoint JwtAuthenticationEntryPoint;

	@MockBean
	RecruiterService recruiterService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	JwtRequestFilter jwtRequestFilter;

	@BeforeEach
	void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).addFilter(jwtRequestFilter)
				.setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void signUpTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/signup").content(getSignupDto()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isOk());
		
		ObjectMapper Obj = new ObjectMapper();
		SignupDto dto = new SignupDto();
		dto.setName("");
		dto.setPassword("");
		dto.setUsername("");
		String jsonStr = Obj.writeValueAsString(dto);
		this.mockMvc
		.perform(MockMvcRequestBuilders.post("/signup").content(jsonStr).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("{\"password\":\"password can't be blank\",\"name\":\"name can't be blank\",\"username\":\"username can't be blank\"}"));
		
		dto.setName("test_user");
		dto.setPassword("password");
		dto.setUsername("test");
		jsonStr = Obj.writeValueAsString(dto);
		this.mockMvc
		.perform(MockMvcRequestBuilders.post("/signup").content(jsonStr).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("{\"username\":\"username should be a valid email address\"}"));
	}

	@Test
	void signUpInternalServerExceptionTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		Mockito.doThrow(NullPointerException.class).when(recruiterService).signup(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/signup").content(getSignupDto()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void signUpExceptionTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		Mockito.doThrow(new NullPointerException("Failed to singup")).when(recruiterService).signup(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/signup").content(getSignupDto()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string("Failed to singup"));
	}

	@Test
	void sendOtpTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/forget-password").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void sendOtpInternalServerExceptionTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		Mockito.doThrow(NullPointerException.class).when(recruiterService).sendOtp(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/forget-password").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void sendOtpExceptionTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		Mockito.doThrow(new NullPointerException("Failed to send otp")).when(recruiterService).sendOtp(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/forget-password").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string("Failed to send otp"));
	}

	@Test
	void verifyOtpTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		when(recruiterService.verifyOtp(any(),any())).thenReturn(Boolean.TRUE);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/verify-otp").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").param("otp", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isOk());
		
		when(recruiterService.verifyOtp(any(),any())).thenReturn(Boolean.FALSE);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/verify-otp").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").param("otp", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isUnauthorized()).andExpect(content().string("Inavlid Otp"));
	}

	@Test
	void verifyOtpInternalServerExceptionTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		Mockito.when(recruiterService.verifyOtp(any(),any())).thenThrow(RecruiterException.class);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/verify-otp").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").param("otp", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void verifyOtpExceptionTest() throws Exception {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
		Mockito.when(recruiterService.verifyOtp(any(),any())).thenThrow(new RecruiterException("Failed to verify otp"));
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/verify-otp").content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.param("username", "").param("otp", "").header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string("Failed to verify otp"));
	}

	private String getSignupDto() {
		ObjectMapper Obj = new ObjectMapper();
		SignupDto dto = new SignupDto();
		dto.setName("test_user");
		dto.setPassword("password");
		dto.setUsername("test@checker.com");
		try {
			String jsonStr = Obj.writeValueAsString(dto);
			return (jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
