package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.GoalPortfolioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalPortfolioService {
	private final GoalPortfolioRepository goalPortfolioRepository;

	public GoalPortfolio createGoalPortfolioAndSave(PortfolioTemplate template, User user) {
		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.user(user)
			.depositWithdrawalRatio(template.getDepositWithdrawalRatio())
			.savingTimeDepositRatio(template.getSavingTimeDepositRatio())
			.investmentRatio(template.getInvestmentRatio())
			.foreignCurrencyRatio(template.getForeignCurrencyRatio())
			.pensionRatio(template.getPensionRatio())
			.etcRatio(template.getEtcRatio())
			.template(template)
			.build();

		return goalPortfolioRepository.save(goalPortfolio);
	}
}
