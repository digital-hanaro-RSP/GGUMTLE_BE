package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.advertisement.AdvertisementResponseDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;
import com.hana4.ggumtle.repository.AdvertisementRepository;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

	@InjectMocks
	private AdvertisementService advertisementService;

	@Mock
	private AdvertisementRepository advertisementRepository;

	@Mock
	private GoalPortfolioService goalPortfolioService;

	private String userId;
	private Advertisement mockAdvertisement;

	@BeforeEach
	void setUp() {
		userId = "testUserId";
		mockAdvertisement = Advertisement.builder()
			.id(1L)
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Test Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("보통위험")
			.yield("10%")
			.link("https://example.com")
			.build();
	}

	@Test
	void testGetMainAd_Conservative() {
		testGetMainAdForInvestmentType("CONSERVATIVE", "매우낮은위험");
	}

	@Test
	void testGetMainAd_ModeratelyConservative() {
		testGetMainAdForInvestmentType("MODERATELY_CONSERVATIVE", "낮은위험");
	}

	@Test
	void testGetMainAd_Balanced() {
		testGetMainAdForInvestmentType("BALANCED", "보통위험");
	}

	@Test
	void testGetMainAd_ModeratelyAggressive() {
		testGetMainAdForInvestmentType("MODERATELY_AGGRESSIVE", "높은위험");
	}

	@Test
	void testGetMainAd_Aggressive() {
		testGetMainAdForInvestmentType("AGGRESSIVE", "매우높은위험");
	}

	private void testGetMainAdForInvestmentType(String investmentType, String expectedRiskRating) {
		GoalPortfolioResponseDto.InvestmentType mockInvestmentType = new GoalPortfolioResponseDto.InvestmentType(
			investmentType);
		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUserId(userId)).thenReturn(mockInvestmentType);
		when(advertisementRepository.findFirstByRiskRating(expectedRiskRating)).thenReturn(
			Optional.of(mockAdvertisement));

		AdvertisementResponseDto.MainAd result = advertisementService.getMainAd(userId);

		assertNotNull(result);
		assertEquals(mockAdvertisement.getId(), result.getId());
		assertEquals(mockAdvertisement.getProductName(), result.getProductName());
		assertEquals(mockAdvertisement.getRiskRating(), result.getRiskRating());
		assertEquals(mockAdvertisement.getYield(), result.getYield());
		assertEquals(mockAdvertisement.getLink(), result.getLink());
	}

	@Test
	void testGetMainAd_InvalidInvestmentType() {
		GoalPortfolioResponseDto.InvestmentType mockInvestmentType = new GoalPortfolioResponseDto.InvestmentType(
			"INVALID");
		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUserId(userId)).thenReturn(mockInvestmentType);

		CustomException exception = assertThrows(CustomException.class, () -> advertisementService.getMainAd(userId));
		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("지원하지 않는 투자성향 타입입니다"));
	}

	@Test
	void testGetMainAd_NoAdvertisementFound() {
		GoalPortfolioResponseDto.InvestmentType mockInvestmentType = new GoalPortfolioResponseDto.InvestmentType(
			"BALANCED");
		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUserId(userId)).thenReturn(mockInvestmentType);
		when(advertisementRepository.findFirstByRiskRating("보통위험")).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () -> advertisementService.getMainAd(userId));
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("해당 위험 등급의 광고가 존재하지 않습니다"));
	}
}
