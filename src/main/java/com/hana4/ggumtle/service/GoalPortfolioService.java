package com.hana4.ggumtle.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioRequestDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
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
	private final MyDataService myDataService;

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

	public GoalPortfolioResponseDto.RecommendGoalPortfolioInfo recommendGoalPortfolio(User user) {
		// 1. 현재 목표 포트폴리오 조회
		GoalPortfolio currentGoal = goalPortfolioRepository.findByUserId(user.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오를 찾을 수 없습니다."));

		// 2. 현재 템플릿이 AGGRESSIVE 이면 false 반환
		String templateName = currentGoal.getTemplate().getName();
		if (templateName.equals("AGGRESSIVE")) {
			return GoalPortfolioResponseDto.RecommendGoalPortfolioInfo.from(false);
		}

		// 3. 1년 이상의 버킷 데이터 필터링
		LocalDate today = LocalDate.now();
		LocalDate oneYearLater = today.plusYears(1);
		List<Bucket> buckets = bucketService.getBucketsDueAfter(user.getId(), oneYearLater);

		if (buckets.isEmpty()) {
			return GoalPortfolioResponseDto.RecommendGoalPortfolioInfo.from(false);
		}

		// 4. 각 버킷의 하루 투자 금액 계산
		BigDecimal totalDailyInvestment = BigDecimal.ZERO;
		for (Bucket bucket : buckets) {
			BigDecimal goalAmount = bucket.getGoalAmount();
			BigDecimal safeBox = bucket.getSafeBox() != null ? bucket.getSafeBox() : BigDecimal.ZERO;

			// getDueDate가 null이면 20년 뒤로 설정
			LocalDate dueDate = bucket.getDueDate() != null ? bucket.getDueDate().toLocalDate() : today.plusYears(20);

			long daysToAchieve = ChronoUnit.DAYS.between(today, dueDate);
			if (daysToAchieve > 0) {
				totalDailyInvestment = totalDailyInvestment.add(
					goalAmount.subtract(safeBox).divide(BigDecimal.valueOf(daysToAchieve), RoundingMode.HALF_UP));
			}
		}

		// 5. 마이데이터에서 총 자산 조회
		BigDecimal totalAssets = myDataService.getTotalAsset(user.getId());

		// 6. 투자 비율에 따라 추천 템플릿 결정
		BigDecimal dailyInvestmentRatio = totalDailyInvestment.multiply(BigDecimal.valueOf(30))
			.divide(totalAssets, RoundingMode.HALF_UP); // 월 기준 비율

		String recommendedTemplate;

		if (dailyInvestmentRatio.compareTo(BigDecimal.valueOf(0.02)) <= 0) {
			recommendedTemplate = "CONSERVATIVE";
		} else if (dailyInvestmentRatio.compareTo(BigDecimal.valueOf(0.07)) <= 0) {
			recommendedTemplate = "MODERATELY_CONSERVATIVE";
		} else if (dailyInvestmentRatio.compareTo(BigDecimal.valueOf(0.15)) <= 0) {
			recommendedTemplate = "BALANCED";
		} else if (dailyInvestmentRatio.compareTo(BigDecimal.valueOf(0.30)) < 0) {
			recommendedTemplate = "MODERATELY_AGGRESSIVE";
		} else {
			recommendedTemplate = "AGGRESSIVE";
		}

		// 수익률 상향 조정이 필요할때만 추천
		List<String> templatePriority = List.of("CONSERVATIVE", "MODERATELY_CONSERVATIVE", "BALANCED",
			"MODERATELY_AGGRESSIVE", "AGGRESSIVE");
		int currentPriority = templatePriority.indexOf(templateName);
		int recommendedPriority = templatePriority.indexOf(recommendedTemplate);

		if (recommendedPriority <= currentPriority) {
			return GoalPortfolioResponseDto.RecommendGoalPortfolioInfo.from(false);
		}

		// 7. 추천 결과 반환
		return GoalPortfolioResponseDto.RecommendGoalPortfolioInfo.from(true, recommendedTemplate,
			dailyInvestmentRatio);
	}

	public GoalPortfolioResponseDto.Ratio applyRecommendation(GoalPortfolioRequestDto.Customize request, User user) {
		GoalPortfolio currentGoal = goalPortfolioRepository.findByUserId(user.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 목표 포트폴리오를 찾을 수 없습니다."));

		PortfolioTemplate template = portfolioTemplateService.findByName(
			request.getCustomizedInvestmentType().toUpperCase());

		return GoalPortfolioResponseDto.Ratio.from(updateCustomizedGoalPortfolioAndSave(currentGoal, template));
	}
}
