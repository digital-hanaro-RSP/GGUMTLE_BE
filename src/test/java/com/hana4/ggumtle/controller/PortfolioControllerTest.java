package com.hana4.ggumtle.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioRequestDto;
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

	@Test
	@WithMockCustomUser
	public void testGetPortfolioTemplates() throws Exception {
		List<GoalPortfolioResponseDto.Template> mockTemplates = List.of(
			GoalPortfolioResponseDto.Template.builder()
				.id(1L)
				.name("CONSERVATIVE")
				.depositWithdrawalRatio(BigDecimal.valueOf(0.00))
				.savingTimeDepositRatio(BigDecimal.valueOf(0.70))
				.investmentRatio(BigDecimal.valueOf(0.20))
				.foreignCurrencyRatio(BigDecimal.valueOf(0.00))
				.pensionRatio(BigDecimal.valueOf(0.10))
				.etcRatio(BigDecimal.valueOf(0.00))
				.build(),
			GoalPortfolioResponseDto.Template.builder()
				.id(2L)
				.name("MODERATELY_CONSERVATIVE")
				.depositWithdrawalRatio(BigDecimal.valueOf(0.00))
				.savingTimeDepositRatio(BigDecimal.valueOf(0.50))
				.investmentRatio(BigDecimal.valueOf(0.30))
				.foreignCurrencyRatio(BigDecimal.valueOf(0.05))
				.pensionRatio(BigDecimal.valueOf(0.15))
				.etcRatio(BigDecimal.valueOf(0.00))
				.build(),
			GoalPortfolioResponseDto.Template.builder()
				.id(3L)
				.name("BALANCED")
				.depositWithdrawalRatio(BigDecimal.valueOf(0.00))
				.savingTimeDepositRatio(BigDecimal.valueOf(0.30))
				.investmentRatio(BigDecimal.valueOf(0.40))
				.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
				.pensionRatio(BigDecimal.valueOf(0.15))
				.etcRatio(BigDecimal.valueOf(0.05))
				.build(),
			GoalPortfolioResponseDto.Template.builder()
				.id(4L)
				.name("MODERATELY_AGGRESSIVE")
				.depositWithdrawalRatio(BigDecimal.valueOf(0.00))
				.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
				.investmentRatio(BigDecimal.valueOf(0.50))
				.foreignCurrencyRatio(BigDecimal.valueOf(0.15))
				.pensionRatio(BigDecimal.valueOf(0.10))
				.etcRatio(BigDecimal.valueOf(0.05))
				.build(),
			GoalPortfolioResponseDto.Template.builder()
				.id(5L)
				.name("AGGRESSIVE")
				.depositWithdrawalRatio(BigDecimal.valueOf(0.00))
				.savingTimeDepositRatio(BigDecimal.valueOf(0.10))
				.investmentRatio(BigDecimal.valueOf(0.60))
				.foreignCurrencyRatio(BigDecimal.valueOf(0.15))
				.pensionRatio(BigDecimal.valueOf(0.05))
				.etcRatio(BigDecimal.valueOf(0.10))
				.build()
		);

		GoalPortfolioResponseDto.GoalTemplateOption mockResponse =
			GoalPortfolioResponseDto.GoalTemplateOption.builder()
				.templates(mockTemplates)
				.build();

		when(goalPortfolioService.getGoalPortfolioTemplate()).thenReturn(mockResponse);

		mockMvc.perform(get("/portfolio/templates")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.templates", hasSize(5)))
			.andExpect(jsonPath("$.data.templates[0].id").value(1))
			.andExpect(jsonPath("$.data.templates[0].name").value("CONSERVATIVE"))
			.andExpect(jsonPath("$.data.templates[0].savingTimeDepositRatio").value(0.70))
			.andExpect(jsonPath("$.data.templates[4].name").value("AGGRESSIVE"))
			.andExpect(jsonPath("$.data.templates[4].investmentRatio").value(0.60));
	}

	@Test
	@WithMockCustomUser
	void recommendGoalPortfolio_success() throws Exception {
		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo mockResponse =
			GoalPortfolioResponseDto.RecommendGoalPortfolioInfo.builder()
				.isRecommended(true)
				.build();

		when(goalPortfolioService.recommendGoalPortfolio(any(User.class)))
			.thenReturn(mockResponse);

		mockMvc.perform(get("/portfolio/recommendation")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.recommended").value(true));
	}

	@Test
	@WithMockCustomUser
	void applyRecommendation_success() throws Exception {
		GoalPortfolioRequestDto.Customize request = GoalPortfolioRequestDto.Customize.builder()
			.customizedInvestmentType("AGGRESSIVE")
			.build();

		GoalPortfolioResponseDto.Ratio mockResponse = GoalPortfolioResponseDto.Ratio.builder()
			.depositWithdrawalRatio(BigDecimal.valueOf(0.00))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.10))
			.investmentRatio(BigDecimal.valueOf(0.60))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.15))
			.pensionRatio(BigDecimal.valueOf(0.05))
			.etcRatio(BigDecimal.valueOf(0.10))
			.id(1L)
			.userId("0815cd32-6392-4495-be98-69eaa839cc0c")
			.build();

		when(goalPortfolioService.applyRecommendation(any(), any())).thenReturn(mockResponse);

		mockMvc.perform(patch("/portfolio/recommendation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.depositWithdrawalRatio").value(0.00))
			.andExpect(jsonPath("$.data.savingTimeDepositRatio").value(0.10))
			.andExpect(jsonPath("$.data.investmentRatio").value(0.60))
			.andExpect(jsonPath("$.data.foreignCurrencyRatio").value(0.15))
			.andExpect(jsonPath("$.data.pensionRatio").value(0.05))
			.andExpect(jsonPath("$.data.etcRatio").value(0.10))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.userId").value("0815cd32-6392-4495-be98-69eaa839cc0c"));

		verify(goalPortfolioService).applyRecommendation(any(), any());
	}

}
