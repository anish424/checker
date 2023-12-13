package com.checker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.checker.configuration.JwtAuthenticationEntryPoint;
import com.checker.configuration.JwtRequestFilter;
import com.checker.configuration.JwtTokenUtil;
import com.checker.exception.JwtException;
import com.checker.exception.RestResponseEntityExceptionHandler;
import com.checker.service.CandidateService;
import com.checker.service.JwtUserDetailsService;

@WebMvcTest(controllers = JwtAuthenticationController.class)
class JwtAuthenticationControllerTest {

	@InjectMocks
	JwtAuthenticationController controller;

	@MockBean
	JwtUserDetailsService jwtUserDetailsService;

	@MockBean
	JwtTokenUtil JwtTokenUtil;

	@MockBean
	JwtAuthenticationEntryPoint JwtAuthenticationEntryPoint;

	@MockBean
	CandidateService CandidateService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	JwtRequestFilter jwtRequestFilter;

	@MockBean
	AuthenticationManager authenticationManager;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(new RestResponseEntityExceptionHandler()).build();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void loginUserInactiveExceptionTest() throws Exception {
		doThrow(new JwtException("User is marked inactive")).when(jwtUserDetailsService).authenticate(any(), any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(loginRequest().toString()))
				.andDo(print()).andExpect(status().isUnauthorized())
				.andExpect(content().string("User is marked inactive"));
	}

	@Test
	void loginUserInvalidCredentialsExceptionTest() throws Exception {
		doThrow(new JwtException("Invalid credentials")).when(jwtUserDetailsService).authenticate(any(), any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(loginRequest().toString()))
				.andDo(print()).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid credentials"));
	}

	@Test
	void loginUserUnathorizedExceptionTest() throws Exception {
		doThrow(new JwtException("Invalid credentials")).when(jwtUserDetailsService).authenticate(any(), any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(loginRequest().toString()))
				.andDo(print()).andExpect(status().isUnauthorized());
	}

	@Test
	void loginUserFailedTest() throws Exception {
		when(authenticationManager.authenticate(any())).thenReturn(null);
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		
		when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.FALSE);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginRequest().toString()))
				.andDo(print()).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid credentials"));
	}

	@Test
	void loginUserTest() throws Exception {
		when(authenticationManager.authenticate(any())).thenReturn(null);
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		
		when(passwordEncoder.matches(any(), any())).thenReturn(Boolean.TRUE);
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginRequest().toString()))
				.andDo(print()).andExpect(status().isOk());
		
		JSONObject data = new JSONObject();
		data.put("username", "");
		data.put("password", "");
		this.mockMvc
		.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(data.toString()))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("{\"password\":\"password can't be blank\",\"username\":\"username can't be blank\"}"));
		
		data.put("username", "test");
		data.put("password", "password");
		this.mockMvc
		.perform(MockMvcRequestBuilders.post("/login").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(data.toString()))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string("{\"username\":\"username should be a valid email address\"}"));
	}

	private JSONObject loginRequest() {
		JSONObject data = new JSONObject();
		data.put("username", "test@checker.com");
		data.put("password", "password");
		return data;
	}

}
