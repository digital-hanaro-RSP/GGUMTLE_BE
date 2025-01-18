package com.hana4.ggumtle.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.dto.survey.SurveyRequestDto;
import com.hana4.ggumtle.dto.survey.SurveyResponseDto;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.service.SurveyService;

@WebMvcTest(
	controllers = SurveyController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	}
)
@Import(TestSecurityConfig.class)
public class SurveyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockitoBean
	public SurveyService surveyService;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.build();
	}

	@Test
	@WithMockCustomUser
	public void testCreateSurvey() throws Exception {
		// Arrange
		SurveyRequestDto.CreateSurvey requestDto = SurveyRequestDto.CreateSurvey.builder()
			.answers(List.of(1, 2, 3, 4, 5))
			.investmentType("BALANCED")
			.build();

		SurveyResponseDto.CreateResponse responseDto = SurveyResponseDto.CreateResponse.builder()
			.id(1L)
			.userId("testUser")
			.answers(List.of(1, 2, 3, 4, 5))
			.build();

		when(surveyService.createSurvey(any(SurveyRequestDto.CreateSurvey.class), any(User.class)))
			.thenReturn(responseDto);

		// Act & Assert
		mockMvc.perform(post("/survey")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.userId").value("testUser"))
			.andExpect(jsonPath("$.data.answers").isArray())
			.andExpect(jsonPath("$.data.answers[0]").value(1))
			.andExpect(jsonPath("$.data.answers[1]").value(2))
			.andExpect(jsonPath("$.data.answers[2]").value(3))
			.andExpect(jsonPath("$.data.answers[3]").value(4))
			.andExpect(jsonPath("$.data.answers[4]").value(5));
	}
}
