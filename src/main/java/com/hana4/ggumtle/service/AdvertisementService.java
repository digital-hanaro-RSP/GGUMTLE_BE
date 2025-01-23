package com.hana4.ggumtle.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.advertisement.AdvertisementResponseDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementAdType;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.AdvertisementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
	private final AdvertisementRepository advertisementRepository;
	private final GoalPortfolioService goalPortfolioService;
	private final GroupService groupService;

	public AdvertisementResponseDto.MainAd getMainAd(User user) {
		GoalPortfolioResponseDto.InvestmentType investmentType = goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(
			user);

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

	public AdvertisementResponseDto.CommunityAd getCommunityAd(Long groupId) {
		Group group = groupService.getGroup(groupId);
		AdvertisementAdType adType = switch (group.getCategory()) {
			case AFTER_RETIREMENT -> AdvertisementAdType.RETIREMENT;
			case EDUCATION -> AdvertisementAdType.EDUCATION;
			case HOBBY -> AdvertisementAdType.HOBBY;
			case INVESTMENT -> AdvertisementAdType.HANA;
			case TRAVEL -> AdvertisementAdType.TRAVEL;
		};
		List<Advertisement> advertisements = advertisementRepository.findAllByAdType(adType);
		Advertisement advertisement = advertisements.get((int)(Math.random() * (advertisements.size() - 1)));
		return AdvertisementResponseDto.CommunityAd.from(advertisement);
	}
}
