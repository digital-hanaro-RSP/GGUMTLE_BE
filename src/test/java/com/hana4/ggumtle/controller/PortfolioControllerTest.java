package com.hana4.ggumtle.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.service.GoalPortfolioService;
import com.hana4.ggumtle.service.MainPortfolioService;

@WebMvcTest(
	controllers = PortfolioController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	}
)
@Import(TestSecurityConfig.class)
public class PortfolioControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	GoalPortfolioService goalPortfolioService;

	@MockitoBean
	MainPortfolioService mainPortfolioService;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.build();
	}

	@Test
	@WithMockCustomUser
	public void testGetUserNameAndInvestmentType() throws Exception {
		GoalPortfolioResponseDto.InvestmentType mockResponse = GoalPortfolioResponseDto.InvestmentType.builder()
			.investmentType("CONSERVATIVE")
			.userName("문서아3")
			.build();

		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(any(User.class))).thenReturn(mockResponse);

		mockMvc.perform(get("/portfolio/investmentType")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userName").value("문서아3"))
			.andExpect(jsonPath("$.data.investmentType").value("CONSERVATIVE"));
	}
}
