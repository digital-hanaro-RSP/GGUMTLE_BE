package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.advertisement.AdvertisementResponseDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.repository.AdvertisementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
	private final AdvertisementRepository advertisementRepository;
	private final GoalPortfolioService goalPortfolioService;

	public AdvertisementResponseDto.MainAd getMainAd(String userId) {
		GoalPortfolioResponseDto.InvestmentType investmentType = goalPortfolioService.getGoalPortfolioInvestmentTypeByUserId(
			userId);

		String riskRating = switch (investmentType.getInvestmentType()) {
			case "CONSERVATIVE" -> "매우낮은위험";
			case "MODERATELY_CONSERVATIVE" -> "낮은위험";
			case "BALANCED" -> "보통위험";
			case "MODERATELY_AGGRESSIVE" -> "높은위험";
			case "AGGRESSIVE" -> "매우높은위험";
			default ->
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "지원하지 않는 투자성향 타입입니다. : " + investmentType);
		};

		Advertisement ad = advertisementRepository.findFirstByRiskRating(riskRating)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 위험 등급의 광고가 존재하지 않습니다"));

		return AdvertisementResponseDto.MainAd.from(ad);
	}
}
