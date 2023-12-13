package com.checker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.checker.configuration.JwtAuthenticationEntryPoint;
import com.checker.configuration.JwtTokenUtil;
import com.checker.configuration.SpringSecurityConfig;
import com.checker.dto.AdjudicationStatus;
import com.checker.dto.ChargeDto;
import com.checker.dto.Status;
import com.checker.exception.CandidateException;
import com.checker.exception.NoticeException;
import com.checker.service.CandidateService;
import com.checker.service.JwtUserDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;

@WebMvcTest(controllers = CandidateController.class)
@Import(SpringSecurityConfig.class)
class CandidateControllerTest {

	@InjectMocks
	CandidateController controller;

	@MockBean
	JwtUserDetailsService jwtUserDetailsService;

	@MockBean
	JwtTokenUtil JwtTokenUtil;

	@MockBean
	JwtAuthenticationEntryPoint JwtAuthenticationEntryPoint;

	@MockBean
	CandidateService candidateService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getCandidatesNoSuchUserExceptionTest() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(null);
		Mockito.when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid user"));
	}

	@Test
	void getCandidatesWithoutJWTTest() throws Exception {
		Mockito.when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void getCandidatesTokenNotValidTest() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(null);
		Mockito.when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		Mockito.when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isUnauthorized()).andExpect(content().string("Token not valid"));
	}

	@Test
	void getCandidatesNoAuthorizationHeaderTest() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(null);
		Mockito.when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		Mockito.when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());

		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearers token"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void cgetCandidatesInternalServerExceptionTest() throws Exception {
		setupJwt();
		when(candidateService.getCandidates(any(), any(), any(), any())).thenThrow(NullPointerException.class);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void getCandidatesExceptionTest() throws Exception {
		setupJwt();
		CandidateException candidateException = new CandidateException("get candiddates failed");
		when(candidateService.getCandidates(any(), any(), any(), any())).thenThrow(candidateException);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("get candiddates failed"));
	}

	@Test
	void getCandidatesWithDefaultValuesTest() throws Exception {
		setupJwt();
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	void getCandidatesJwtExceptionTest() throws Exception {
		Mockito.when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenThrow(IllegalArgumentException.class);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(content().string("Unable to get JWT Token"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void getCandidatesExpiredJwtExceptionTest() throws Exception {
		Mockito.when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenThrow(ExpiredJwtException.class);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isUnauthorized())
				.andExpect(content().string("JWT Token has expired"));

	}

	@Test
	void getCandidatesTest() throws Exception {
		setupJwt();
		List<String> status = new ArrayList<>();
		status.add(Status.CLEAR.getValue());
		status.add(Status.CONSIDER.getValue());
		String[] statusArray = status.toArray(new String[0]);

		List<String> adjudictaionList = new ArrayList<>();
		adjudictaionList.add(AdjudicationStatus.ENGAGED.getValue());
		adjudictaionList.add(AdjudicationStatus.PRE_ADVERSE_ACTION.getValue());
		String[] adjudicationArray = adjudictaionList.toArray(new String[0]);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token").param("pageSize", "2").param("pageNo", "2")
				.param("searchKeyword", "John").param("status", statusArray).param("adjudication", adjudicationArray))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void getCandidatesWithAllStatusTest() throws Exception {
		setupJwt();
		List<String> status = new ArrayList<>();
		status.add(Status.ALL_STATUS.getValue());
		String[] statusArray = status.toArray(new String[0]);

		List<String> adjudictaionList = new ArrayList<>();
		adjudictaionList.add(AdjudicationStatus.ALL.getValue());
		String[] adjudicationArray = adjudictaionList.toArray(new String[0]);
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates").accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token").param("pageSize", "2").param("pageNo", "2")
				.param("searchKeyword", "John").param("status", statusArray).param("adjudication", adjudicationArray))
				.andDo(print()).andExpect(status().isOk());

	}

	@Test
	void getCandidateTest() throws Exception {
		setupJwt();
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates/1").accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token")).andDo(print()).andExpect(status().isOk());
	}

	@Test
	void getCandidateInternalServerExceptionTest() throws Exception {
		setupJwt();
		when(candidateService.getCandidate(any())).thenThrow(NullPointerException.class);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates/1").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void getCandidateExceptionTest() throws Exception {
		setupJwt();
		when(candidateService.getCandidate(any())).thenThrow(new NullPointerException("Failed to get candidate"));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates/1").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Failed to get candidate"));
	}

	@Test
	void getNoticeTest() throws Exception {
		setupJwt();
		this.mockMvc.perform(MockMvcRequestBuilders.get("/candidates/1/notice").accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer token")).andDo(print()).andExpect(status().isOk());
	}

	private void setupJwt() {
		when(JwtTokenUtil.getUsernameFromToken(Mockito.any())).thenReturn("test_user");
		UserDetails user = Mockito.mock(UserDetails.class);
		when(jwtUserDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);
		when(JwtTokenUtil.validateToken(any(), any())).thenReturn(Boolean.TRUE);
	}

	@Test
	void getNoticeInternalServerExceptionTest() throws Exception {
		setupJwt();
		Mockito.doThrow(NullPointerException.class).when(candidateService).getNotice(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates/1/notice").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void getNoticeExceptionTest() throws Exception {
		setupJwt();
		Mockito.doThrow(new NoticeException("Failed to create Notice")).when(candidateService).getNotice(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates/1/notice").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Failed to create Notice"));
	}

	@Test
	void getNoticeException1Test() throws Exception {
		setupJwt();
		Mockito.doThrow(NoticeException.class).when(candidateService).getNotice(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/candidates/1/notice").accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void sendNoticeTest() throws Exception {
		setupJwt();
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/candidates/1/adverse-action").content(getChargeDtoList())
						.param("autoSendDuration", "1").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void sendNoticeInternalServerExceptionTest() throws Exception {
		setupJwt();
		Mockito.doThrow(NullPointerException.class).when(candidateService).sendNotice(any(), any(), any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/candidates/1/adverse-action").content(getChargeDtoList())
						.param("autoSendDuration", "1").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void sendNoticeExceptionTest() throws Exception {
		setupJwt();
		Mockito.doThrow(new NullPointerException("Failed to send Notice")).when(candidateService).sendNotice(any(),
				any(), any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/candidates/1/adverse-action").content(getChargeDtoList())
						.param("autoSendDuration", "1").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Failed to send Notice"));
	}

	@Test
	void engageTest() throws Exception {
		setupJwt();
		this.mockMvc.perform(
				MockMvcRequestBuilders.post("/candidates/1/engage").content("").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isOk());
	}

	@Test
	void engageInternalServerExceptionTest() throws Exception {
		setupJwt();
		Mockito.doThrow(NullPointerException.class).when(candidateService).engage(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/candidates/1/engage").content("")
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Internal Server error, Please try again later"));
	}

	@Test
	void engageExceptionTest() throws Exception {
		setupJwt();
		Mockito.doThrow(new NullPointerException("Failed to engage with candidate")).when(candidateService)
				.engage(any());
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/candidates/1/engage").content("")
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token"))
				.andDo(print()).andExpect(status().isInternalServerError())
				.andExpect(content().string("Failed to engage with candidate"));
	}

	private String getChargeDtoList() {
		List<ChargeDto> charges = new ArrayList<>();
		ChargeDto dto = new ChargeDto();
		charges.add(dto);
		ObjectMapper obj = new ObjectMapper();
		String json = "";
		try {
			json = obj.writeValueAsString(charges);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

}
