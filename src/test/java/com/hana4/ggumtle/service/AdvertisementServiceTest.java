package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementAdType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.AdvertisementRepository;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

	@InjectMocks
	private AdvertisementService advertisementService;

	@Mock
	private AdvertisementRepository advertisementRepository;

	@Mock
	private GoalPortfolioService goalPortfolioService;

	@Mock
	private GroupService groupService;

	private List<Advertisement> mockAdvertisements;

	@BeforeEach
	void setUp() {
		mockAdvertisements = Arrays.asList(
			Advertisement.builder()
				.id(1L)
				.productType(AdvertisementProductType.INVESTMENT)
				.productName("Test Product 1")
				.locationType(AdvertisementLocationType.MAIN)
				.riskRating("보통위험")
				.yield("10%")
				.link("https://example1.com")
				.build(),
			Advertisement.builder()
				.id(2L)
				.productType(AdvertisementProductType.PENSION)
				.productName("Test Product 2")
				.locationType(AdvertisementLocationType.MAIN)
				.riskRating("높은위험")
				.yield("15%")
				.link("https://example2.com")
				.build()
		);
	}

	@Test
	void testGetMainAd_Conservative() {
		testGetMainAdForInvestmentType("CONSERVATIVE", Arrays.asList("매우낮은위험", "낮은위험", "보통위험"));
	}

	@Test
	void testGetMainAd_ModeratelyConservative() {
		testGetMainAdForInvestmentType("MODERATELY_CONSERVATIVE", Arrays.asList("낮은위험", "보통위험", "높은위험"));
	}

	@Test
	void testGetMainAd_Balanced() {
		testGetMainAdForInvestmentType("BALANCED", Arrays.asList("보통위험", "낮은위험", "높은위험"));
	}

	@Test
	void testGetMainAd_ModeratelyAggressive() {
		testGetMainAdForInvestmentType("MODERATELY_AGGRESSIVE", Arrays.asList("높은위험", "보통위험", "매우높은위험"));
	}

	@Test
	void testGetMainAd_Aggressive() {
		testGetMainAdForInvestmentType("AGGRESSIVE", Arrays.asList("매우높은위험", "높은위험", "보통위험"));
	}

	private void testGetMainAdForInvestmentType(String investmentType, List<String> expectedRiskRatings) {
		User user = createTestUser();

		GoalPortfolioResponseDto.InvestmentType mockInvestmentType = mock(
			GoalPortfolioResponseDto.InvestmentType.class);
		when(mockInvestmentType.getInvestmentType()).thenReturn(investmentType);
		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(user)).thenReturn(mockInvestmentType);
		when(advertisementRepository.findAllByRiskRatingInOrderByIdDesc(expectedRiskRatings)).thenReturn(
			mockAdvertisements);

		AdvertisementResponseDto.MainAdList result = advertisementService.getMainAd(user);

		assertNotNull(result);
		assertEquals(2, result.getMainAds().size());
		assertEquals(mockAdvertisements.get(0).getId(), result.getMainAds().get(0).getId());
		assertEquals(mockAdvertisements.get(1).getId(), result.getMainAds().get(1).getId());
	}

	@Test
	void testGetMainAd_InvalidInvestmentType() {
		User user = createTestUser();

		GoalPortfolioResponseDto.InvestmentType mockInvestmentType = mock(
			GoalPortfolioResponseDto.InvestmentType.class);
		when(mockInvestmentType.getInvestmentType()).thenReturn("INVALID");
		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(user)).thenReturn(mockInvestmentType);

		CustomException exception = assertThrows(CustomException.class, () -> advertisementService.getMainAd(user));
		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("지원하지 않는 투자성향 타입입니다"));
	}

	@Test
	void testGetMainAd_NoAdvertisementFound() {
		User user = createTestUser();

		GoalPortfolioResponseDto.InvestmentType mockInvestmentType = mock(
			GoalPortfolioResponseDto.InvestmentType.class);
		when(mockInvestmentType.getInvestmentType()).thenReturn("BALANCED");
		when(goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(user)).thenReturn(mockInvestmentType);
		when(advertisementRepository.findAllByRiskRatingInOrderByIdDesc(anyList())).thenReturn(Arrays.asList());

		AdvertisementResponseDto.MainAdList result = advertisementService.getMainAd(user);
		assertNotNull(result);
		assertTrue(result.getMainAds().isEmpty());
	}

	private User createTestUser() {
		return User.builder()
			.id("27295730-41ce-4df8-9864-4da1fa3c6caa")
			.name("문서아")
			.tel("01012341234")
			.password("password")
			.birthDate(LocalDateTime.of(2000, 1, 1, 0, 0))
			.gender("f")
			.nickname("익명의고라니")
			.role(UserRole.USER)
			.permission((short)0)
			.build();
	}

	@Test
	void getCommunityAd() {
		Group group = new Group(
			1L, // id
			"여행자 모임", // name
			GroupCategory.INVESTMENT, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		Group group2 = new Group(
			1L, // id
			"여행자 모임", // name
			GroupCategory.EDUCATION, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		Group group3 = new Group(
			1L, // id
			"여행자 모임", // name
			GroupCategory.AFTER_RETIREMENT, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		Group group4 = new Group(
			1L, // id
			"여행자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		Group group5 = new Group(
			1L, // id
			"여행자 모임", // name
			GroupCategory.HOBBY, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);
		List<Advertisement> hanaAds = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			AdvertisementAdType adType = AdvertisementAdType.INVESTMENT;
			hanaAds.add(
				Advertisement.builder()
					.locationType(AdvertisementLocationType.COMMUNITY)
					.adType(adType)
					.bannerImageUrl("https://ggumtlebucket.s3.ap-northeast-2.amazonaws.com/hanainvest.png")
					.link("https://www.hanaw.com/main/main/index.cmd")
					.build());
		}

		List<Advertisement> hobbyAds = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			AdvertisementAdType adType = AdvertisementAdType.HOBBY;
			hobbyAds.add(
				Advertisement.builder()
					.locationType(AdvertisementLocationType.COMMUNITY)
					.adType(adType)
					.bannerImageUrl("https://ggumtlebucket.s3.ap-northeast-2.amazonaws.com/hanainvest.png")
					.link("https://www.hanaw.com/main/main/index.cmd")
					.build());
		}

		List<Advertisement> travelAds = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			AdvertisementAdType adType = AdvertisementAdType.TRAVEL;
			travelAds.add(
				Advertisement.builder()
					.locationType(AdvertisementLocationType.COMMUNITY)
					.adType(adType)
					.bannerImageUrl("https://ggumtlebucket.s3.ap-northeast-2.amazonaws.com/hanainvest.png")
					.link("https://www.hanaw.com/main/main/index.cmd")
					.build());
		}

		List<Advertisement> eduAds = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			AdvertisementAdType adType = AdvertisementAdType.EDUCATION;
			eduAds.add(
				Advertisement.builder()
					.locationType(AdvertisementLocationType.COMMUNITY)
					.adType(adType)
					.bannerImageUrl("https://ggumtlebucket.s3.ap-northeast-2.amazonaws.com/hanainvest.png")
					.link("https://www.hanaw.com/main/main/index.cmd")
					.build());
		}

		List<Advertisement> retireAds = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			AdvertisementAdType adType = AdvertisementAdType.RETIREMENT;
			retireAds.add(
				Advertisement.builder()
					.locationType(AdvertisementLocationType.COMMUNITY)
					.adType(adType)
					.bannerImageUrl("https://ggumtlebucket.s3.ap-northeast-2.amazonaws.com/hanainvest.png")
					.link("https://www.hanaw.com/main/main/index.cmd")
					.build());
		}

		List<Advertisement> advertisements = new ArrayList<>();
		when(groupService.getGroup(1L)).thenReturn(group);
		when(groupService.getGroup(2L)).thenReturn(group2);
		when(groupService.getGroup(3L)).thenReturn(group3);
		when(groupService.getGroup(4L)).thenReturn(group4);
		when(groupService.getGroup(5L)).thenReturn(group5);
		when(advertisementRepository.findAllByAdType(AdvertisementAdType.INVESTMENT)).thenReturn(hanaAds);
		when(advertisementRepository.findAllByAdType(AdvertisementAdType.RETIREMENT)).thenReturn(retireAds);
		when(advertisementRepository.findAllByAdType(AdvertisementAdType.TRAVEL)).thenReturn(travelAds);
		when(advertisementRepository.findAllByAdType(AdvertisementAdType.EDUCATION)).thenReturn(eduAds);
		when(advertisementRepository.findAllByAdType(AdvertisementAdType.HOBBY)).thenReturn(hobbyAds);

		advertisementService.getCommunityAd(1L);
		advertisementService.getCommunityAd(2L);
		advertisementService.getCommunityAd(3L);
		advertisementService.getCommunityAd(4L);
		advertisementService.getCommunityAd(5L);
		verify(advertisementRepository).findAllByAdType(AdvertisementAdType.INVESTMENT);
		verify(advertisementRepository).findAllByAdType(AdvertisementAdType.HOBBY);
		verify(advertisementRepository).findAllByAdType(AdvertisementAdType.TRAVEL);
		verify(advertisementRepository).findAllByAdType(AdvertisementAdType.RETIREMENT);
		verify(advertisementRepository).findAllByAdType(AdvertisementAdType.EDUCATION);
	}

}
