package com.hana4.ggumtle.service;

import java.util.Arrays;
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

	public AdvertisementResponseDto.MainAdList getMainAd(User user) {
		GoalPortfolioResponseDto.InvestmentType investmentType = goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(
			user);

		List<String> riskRatings = switch (investmentType.getInvestmentType()) {
			case "CONSERVATIVE" -> Arrays.asList("매우낮은위험", "낮은위험", "보통위험");
			case "MODERATELY_CONSERVATIVE" -> Arrays.asList("낮은위험", "보통위험", "높은위험");
			case "BALANCED" -> Arrays.asList("보통위험", "낮은위험", "높은위험");
			case "MODERATELY_AGGRESSIVE" -> Arrays.asList("높은위험", "보통위험", "매우높은위험");
			case "AGGRESSIVE" -> Arrays.asList("매우높은위험", "높은위험", "보통위험");
			default ->
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "지원하지 않는 투자성향 타입입니다. : " + investmentType);
		};

		List<Advertisement> ads = advertisementRepository.findAllByRiskRatingInOrderByIdDesc(riskRatings);

		return AdvertisementResponseDto.MainAdList.from(ads);
	}

	public AdvertisementResponseDto.CommunityAd getCommunityAd(Long groupId) {
		Group group = groupService.getGroup(groupId);
		AdvertisementAdType adType = switch (group.getCategory()) {
			case AFTER_RETIREMENT -> AdvertisementAdType.RETIREMENT;
			case EDUCATION -> AdvertisementAdType.EDUCATION;
			case HOBBY -> AdvertisementAdType.HOBBY;
			case INVESTMENT -> AdvertisementAdType.INVESTMENT;
			case TRAVEL -> AdvertisementAdType.TRAVEL;
		};
		List<Advertisement> advertisements = advertisementRepository.findAllByAdType(adType);
		Advertisement advertisement = advertisements.get((int)(Math.random() * (advertisements.size() - 1)));
		return AdvertisementResponseDto.CommunityAd.from(advertisement);
	}
}
