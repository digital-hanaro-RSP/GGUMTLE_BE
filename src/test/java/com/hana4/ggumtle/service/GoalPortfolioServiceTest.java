package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.GoalPortfolioRepository;

@ExtendWith(MockitoExtension.class)
class GoalPortfolioServiceTest {

	@Mock
	private GoalPortfolioRepository goalPortfolioRepository;

	@InjectMocks
	private GoalPortfolioService goalPortfolioService;

	private PortfolioTemplate template;
	private User user;

	@BeforeEach
	void setUp() {
		template = new PortfolioTemplate();
		template.setDepositWithdrawalRatio(BigDecimal.valueOf(0.30));
		template.setSavingTimeDepositRatio(BigDecimal.valueOf(0.20));
		template.setInvestmentRatio(BigDecimal.valueOf(0.20));
		template.setForeignCurrencyRatio(BigDecimal.valueOf(0.10));
		template.setPensionRatio(BigDecimal.valueOf(0.15));
		template.setEtcRatio(BigDecimal.valueOf(0.05));

		user = new User();
		user.setId("testUser");
	}

	@Test
	void createGoalPortfolioAndSave_ValidInputs_ReturnsGoalPortfolio() {
		GoalPortfolio expectedGoalPortfolio = GoalPortfolio.builder()
			.user(user)
			.depositWithdrawalRatio(BigDecimal.valueOf(0.30))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
			.investmentRatio(BigDecimal.valueOf(0.20))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.15))
			.etcRatio(BigDecimal.valueOf(0.05))
			.template(template)
			.build();

		when(goalPortfolioRepository.save(any(GoalPortfolio.class))).thenReturn(expectedGoalPortfolio);

		GoalPortfolio result = goalPortfolioService.createGoalPortfolioAndSave(template, user);

		assertNotNull(result);
		assertEquals(user, result.getUser());
		assertEquals(template, result.getTemplate());
		assertEquals(BigDecimal.valueOf(0.30), result.getDepositWithdrawalRatio());
		assertEquals(BigDecimal.valueOf(0.20), result.getSavingTimeDepositRatio());
		assertEquals(BigDecimal.valueOf(0.20), result.getInvestmentRatio());
		assertEquals(BigDecimal.valueOf(0.10), result.getForeignCurrencyRatio());
		assertEquals(BigDecimal.valueOf(0.15), result.getPensionRatio());
		assertEquals(BigDecimal.valueOf(0.05), result.getEtcRatio());

		verify(goalPortfolioRepository).save(any(GoalPortfolio.class));
	}

	@Test
	void getGoalPortfolio_성공() {
		User user = new User();
		user.setId("1");
		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.id(1L)
			.depositWithdrawalRatio(BigDecimal.ONE)
			.savingTimeDepositRatio(BigDecimal.ONE)
			.investmentRatio(BigDecimal.ONE)
			.foreignCurrencyRatio(BigDecimal.ONE)
			.pensionRatio(BigDecimal.ONE)
			.etcRatio(BigDecimal.ONE)
			.template(template)
			.user(user)
			.build();
		when(goalPortfolioRepository.findByUserId("1")).thenReturn(Optional.of(goalPortfolio));
		assertThat(goalPortfolioService.getGoalPortfolioByUserId("1").getDepositWithdrawalRatio()).isEqualTo(
			goalPortfolio.getDepositWithdrawalRatio());
	}

	@Test
	void getGoalPortfolio_실패() {
		User user = new User();
		user.setId("1");

		CustomException exception = assertThrows(CustomException.class, () -> {
			goalPortfolioService.getGoalPortfolioByUserId(user.getId());
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오가 존재하지 않습니다.", exception.getMessage());
		verify(goalPortfolioRepository).findByUserId(user.getId());
	}

	@Test
	void getGoalPortfolioInvestmentTypeByUserId_Success() {
		String userId = "testUser";
		User user = User.builder().id(userId).build();
		PortfolioTemplate template = PortfolioTemplate.builder()
			.name("BALANCED")
			.depositWithdrawalRatio(BigDecimal.valueOf(0.30))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
			.investmentRatio(BigDecimal.valueOf(0.20))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.15))
			.etcRatio(BigDecimal.valueOf(0.05))
			.build();

		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.user(user)
			.depositWithdrawalRatio(BigDecimal.valueOf(0.30))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
			.investmentRatio(BigDecimal.valueOf(0.20))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.15))
			.etcRatio(BigDecimal.valueOf(0.05))
			.template(template)
			.build();

		when(goalPortfolioRepository.findByUserId(userId)).thenReturn(Optional.of(goalPortfolio));

		GoalPortfolioResponseDto.InvestmentType result = goalPortfolioService.getGoalPortfolioInvestmentTypeByUserId(
			userId);

		assertNotNull(result);
		assertEquals("BALANCED", result.getInvestmentType());
		verify(goalPortfolioRepository).findByUserId(userId);
	}

	@Test
	void getGoalPortfolioInvestmentTypeByUserId_UserNotFound() {
		String userId = "nonExistentUser";
		when(goalPortfolioRepository.findByUserId(userId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () ->
			goalPortfolioService.getGoalPortfolioInvestmentTypeByUserId(userId)
		);

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오가 존재하지 않습니다.", exception.getMessage());
		verify(goalPortfolioRepository).findByUserId(userId);
	}

}
