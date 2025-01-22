package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
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

	public GoalPortfolioResponseDto.Ratio getGoalPortfolioByUserId(String userId) {
		return GoalPortfolioResponseDto.Ratio.from(goalPortfolioRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오가 존재하지 않습니다.")));
	}

	public GoalPortfolioResponseDto.InvestmentType getGoalPortfolioInvestmentTypeByUserId(String userId) {
		return GoalPortfolioResponseDto.InvestmentType.from(goalPortfolioRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오가 존재하지 않습니다.")));
	}
}
