package com.hana4.ggumtle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioRequestDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.GoalPortfolioRepository;
import com.hana4.ggumtle.repository.PortfolioTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalPortfolioService {
	private final GoalPortfolioRepository goalPortfolioRepository;
	private final PortfolioTemplateRepository portfolioTemplateRepository;
	private final BucketService bucketService;
	private final PortfolioTemplateService portfolioTemplateService;

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

	public GoalPortfolio updateCustomizedGoalPortfolioAndSave(GoalPortfolio currentGoal, PortfolioTemplate template) {

		currentGoal.setDepositWithdrawalRatio(template.getDepositWithdrawalRatio());
		currentGoal.setSavingTimeDepositRatio(template.getSavingTimeDepositRatio());
		currentGoal.setInvestmentRatio(template.getInvestmentRatio());
		currentGoal.setForeignCurrencyRatio(template.getForeignCurrencyRatio());
		currentGoal.setPensionRatio(template.getPensionRatio());
		currentGoal.setEtcRatio(template.getEtcRatio());
		currentGoal.setCustomizedTemplate(template);

		return goalPortfolioRepository.save(currentGoal);
	}

	public GoalPortfolioResponseDto.Ratio getGoalPortfolioByUserId(String userId) {
		return GoalPortfolioResponseDto.Ratio.from(goalPortfolioRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오가 존재하지 않습니다.")));
	}

	public GoalPortfolioResponseDto.InvestmentType getGoalPortfolioInvestmentTypeByUser(User user) {
		return GoalPortfolioResponseDto.InvestmentType.from(goalPortfolioRepository.findByUserId(user.getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오가 존재하지 않습니다.")),
			user.getName());
	}

	public GoalPortfolioResponseDto.GoalTemplateOption getGoalPortfolioTemplate() {
		List<PortfolioTemplate> portfolioTemplates = portfolioTemplateRepository.findAll();

		return GoalPortfolioResponseDto.GoalTemplateOption.from(portfolioTemplates);
	}

	// public GoalPortfolioResponseDto.RecommendGoalPortfolioInfo recommendGoalPortfolio(User user) {
	//
	// }

	public GoalPortfolioResponseDto.Ratio applyRecommendation(GoalPortfolioRequestDto.Customize request, User user) {
		GoalPortfolio currentGoal = goalPortfolioRepository.findByUserId(user.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오를 찾을 수 없습니다."));

		PortfolioTemplate template = portfolioTemplateService.findByName(
			request.getCustomizedInvestmentType().toUpperCase());

		return GoalPortfolioResponseDto.Ratio.from(updateCustomizedGoalPortfolioAndSave(currentGoal, template));
	}
}
