package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
