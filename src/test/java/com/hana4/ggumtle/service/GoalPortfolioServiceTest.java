package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class GoalPortfolioServiceTest {

	@Mock
	private GoalPortfolioRepository goalPortfolioRepository;

	@Mock
	private PortfolioTemplateRepository portfolioTemplateRepository;

	@InjectMocks
	private GoalPortfolioService goalPortfolioService;

	@Mock
	private BucketService bucketService;

	@Mock
	private MyDataService myDataService;

	@Mock
	private PortfolioTemplateService portfolioTemplateService;

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

	@Test
	void getGoalPortfolio_성공() {
		User user = new User();
		user.setId("1");
		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.id(1L)
			.depositWithdrawalRatio(BigDecimal.ONE)
			.savingTimeDepositRatio(BigDecimal.ONE)
			.investmentRatio(BigDecimal.ONE)
			.foreignCurrencyRatio(BigDecimal.ONE)
			.pensionRatio(BigDecimal.ONE)
			.etcRatio(BigDecimal.ONE)
			.template(template)
			.user(user)
			.build();
		when(goalPortfolioRepository.findByUserId("1")).thenReturn(Optional.of(goalPortfolio));
		assertThat(goalPortfolioService.getGoalPortfolioByUserId("1").getDepositWithdrawalRatio()).isEqualTo(
			goalPortfolio.getDepositWithdrawalRatio());
	}

	@Test
	void getGoalPortfolio_실패() {
		User user = new User();
		user.setId("1");

		CustomException exception = assertThrows(CustomException.class, () -> {
			goalPortfolioService.getGoalPortfolioByUserId(user.getId());
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오가 존재하지 않습니다.", exception.getMessage());
		verify(goalPortfolioRepository).findByUserId(user.getId());
	}

	@Test
	void getGoalPortfolioInvestmentTypeByUserId_Success() {
		user = new User();
		user.setId("testUser");
		user.setName("testUserName");
		PortfolioTemplate template = PortfolioTemplate.builder()
			.name("BALANCED")
			.depositWithdrawalRatio(BigDecimal.valueOf(0.30))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
			.investmentRatio(BigDecimal.valueOf(0.20))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.15))
			.etcRatio(BigDecimal.valueOf(0.05))
			.build();

		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.user(user)
			.depositWithdrawalRatio(BigDecimal.valueOf(0.30))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
			.investmentRatio(BigDecimal.valueOf(0.20))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.15))
			.etcRatio(BigDecimal.valueOf(0.05))
			.template(template)
			.build();

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(goalPortfolio));

		GoalPortfolioResponseDto.InvestmentType result = goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(
			user);

		assertNotNull(result);
		assertEquals("BALANCED", result.getInvestmentType());
		verify(goalPortfolioRepository).findByUserId(user.getId());
	}

	@Test
	void getGoalPortfolioInvestmentTypeByUserId_UserNotFound() {
		user = new User();
		user.setId("testUser");
		user.setName("testUserName");
		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () ->
			goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(user)
		);

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오가 존재하지 않습니다.", exception.getMessage());
		verify(goalPortfolioRepository).findByUserId(user.getId());
	}

	@Test
	void getGoalPortfolioInvestmentTypeByUser_Success() {
		User user = new User();
		user.setId("testUser");
		user.setName("testUserName");

		PortfolioTemplate template = PortfolioTemplate.builder()
			.name("BALANCED")
			.build();

		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.user(user)
			.template(template)
			.build();

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(goalPortfolio));

		GoalPortfolioResponseDto.InvestmentType result = goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(
			user);

		assertNotNull(result);
		assertEquals("BALANCED", result.getInvestmentType());
		assertEquals("testUserName", result.getUserName());
		verify(goalPortfolioRepository).findByUserId(user.getId());
	}

	@Test
	void getGoalPortfolioInvestmentTypeByUser_UserNotFound() {
		User user = new User();
		user.setId("testUser");
		user.setName("testUserName");

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () ->
			goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(user)
		);

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오가 존재하지 않습니다.", exception.getMessage());
		verify(goalPortfolioRepository).findByUserId(user.getId());
	}

	@Test
	void getGoalPortfolioTemplate_ReturnsAllTemplates() {
		List<PortfolioTemplate> templates = Arrays.asList(
			PortfolioTemplate.builder().id(1L).name("CONSERVATIVE").build(),
			PortfolioTemplate.builder().id(2L).name("BALANCED").build(),
			PortfolioTemplate.builder().id(3L).name("AGGRESSIVE").build()
		);

		when(portfolioTemplateRepository.findAll()).thenReturn(templates);

		GoalPortfolioResponseDto.GoalTemplateOption result = goalPortfolioService.getGoalPortfolioTemplate();

		assertNotNull(result);
		assertEquals(3, result.getTemplates().size());
		assertTrue(result.getTemplates().stream().anyMatch(t -> t.getName().equals("CONSERVATIVE")));
		assertTrue(result.getTemplates().stream().anyMatch(t -> t.getName().equals("BALANCED")));
		assertTrue(result.getTemplates().stream().anyMatch(t -> t.getName().equals("AGGRESSIVE")));

		verify(portfolioTemplateRepository).findAll();
	}

	@Test
	void applyRecommendation_Success() {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolioRequestDto.Customize request = GoalPortfolioRequestDto.Customize.builder()
			.customizedInvestmentType("AGGRESSIVE")
			.build();

		GoalPortfolio currentGoal = new GoalPortfolio();
		currentGoal.setId(1L);
		currentGoal.setUser(user);

		PortfolioTemplate template = new PortfolioTemplate();
		template.setName("AGGRESSIVE");
		template.setDepositWithdrawalRatio(BigDecimal.valueOf(0.10));
		template.setSavingTimeDepositRatio(BigDecimal.valueOf(0.10));
		template.setInvestmentRatio(BigDecimal.valueOf(0.50));
		template.setForeignCurrencyRatio(BigDecimal.valueOf(0.10));
		template.setPensionRatio(BigDecimal.valueOf(0.10));
		template.setEtcRatio(BigDecimal.valueOf(0.10));

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(portfolioTemplateService.findByName(anyString())).thenReturn(template);
		when(goalPortfolioRepository.save(any(GoalPortfolio.class))).thenAnswer(invocation -> {
			GoalPortfolio savedGoalPortfolio = invocation.getArgument(0);
			savedGoalPortfolio.setId(1L);
			savedGoalPortfolio.setUser(user);
			return savedGoalPortfolio;
		});

		GoalPortfolioResponseDto.Ratio result = goalPortfolioService.applyRecommendation(request, user);

		assertNotNull(result);
		assertEquals(BigDecimal.valueOf(0.10), result.getDepositWithdrawalRatio());
		assertEquals(BigDecimal.valueOf(0.10), result.getSavingTimeDepositRatio());
		assertEquals(BigDecimal.valueOf(0.50), result.getInvestmentRatio());
		assertEquals(BigDecimal.valueOf(0.10), result.getForeignCurrencyRatio());
		assertEquals(BigDecimal.valueOf(0.10), result.getPensionRatio());
		assertEquals(BigDecimal.valueOf(0.10), result.getEtcRatio());

		verify(goalPortfolioRepository).findByUserId(user.getId());
		verify(portfolioTemplateService).findByName("AGGRESSIVE");
		verify(goalPortfolioRepository).save(any(GoalPortfolio.class));
	}

	// 여기
	@Test
	void recommendGoalPortfolio_CurrentTemplateIsAggressive() {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolio currentGoal = new GoalPortfolio();
		PortfolioTemplate aggressiveTemplate = new PortfolioTemplate();
		aggressiveTemplate.setName("AGGRESSIVE");
		currentGoal.setTemplate(aggressiveTemplate);

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertFalse(result.isRecommended());
		verify(goalPortfolioRepository).findByUserId(user.getId());
		verifyNoInteractions(bucketService, myDataService);
	}

	@Test
	void recommendGoalPortfolio_NoBucketsFound() {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolio currentGoal = new GoalPortfolio();
		PortfolioTemplate balancedTemplate = new PortfolioTemplate();
		balancedTemplate.setName("BALANCED");
		currentGoal.setTemplate(balancedTemplate);

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(bucketService.getBucketsDueAfter(eq(user.getId()), any(LocalDate.class))).thenReturn(
			Collections.emptyList());

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertFalse(result.isRecommended());
		verify(goalPortfolioRepository).findByUserId(user.getId());
		verify(bucketService).getBucketsDueAfter(eq(user.getId()), any(LocalDate.class));
		verifyNoInteractions(myDataService);
	}

	@Test
	void recommendGoalPortfolio_RecommendationNeeded() {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolio currentGoal = new GoalPortfolio();
		PortfolioTemplate balancedTemplate = new PortfolioTemplate();
		balancedTemplate.setName("BALANCED");
		currentGoal.setTemplate(balancedTemplate);

		Bucket bucket = new Bucket();
		bucket.setGoalAmount(BigDecimal.valueOf(1000000));
		bucket.setSafeBox(BigDecimal.valueOf(1000));
		bucket.setDueDate(LocalDateTime.now().plusYears(2));

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(bucketService.getBucketsDueAfter(eq(user.getId()), any(LocalDate.class))).thenReturn(
			Collections.singletonList(bucket));
		when(myDataService.getTotalAsset(user.getId())).thenReturn(BigDecimal.valueOf(50000));

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertTrue(result.isRecommended());
		assertNotNull(result.getInvestmentType());
		assertNotNull(result.getEstimatedInvestRatio());

		System.out.println("Recommended: " + true);
		System.out.println("Investment Type: " + result.getInvestmentType());
		System.out.println("Estimated Invest Ratio: " + result.getEstimatedInvestRatio());

		verify(goalPortfolioRepository).findByUserId(user.getId());
		verify(bucketService).getBucketsDueAfter(eq(user.getId()), any(LocalDate.class));
		verify(myDataService).getTotalAsset(user.getId());
	}

	private void testRecommendation(BigDecimal monthlyInvestmentRatio, String expectedTemplate) {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolio currentGoal = new GoalPortfolio();
		PortfolioTemplate currentTemplate = new PortfolioTemplate();
		currentTemplate.setName("CUSTOM");
		currentGoal.setTemplate(currentTemplate);

		BigDecimal totalAssets = BigDecimal.valueOf(100000);
		BigDecimal dailyInvestment = monthlyInvestmentRatio.multiply(totalAssets)
			.divide(BigDecimal.valueOf(30), 10, RoundingMode.HALF_UP);

		Bucket bucket = new Bucket();
		bucket.setGoalAmount(dailyInvestment.multiply(BigDecimal.valueOf(365)));
		bucket.setSafeBox(BigDecimal.ZERO);
		bucket.setDueDate(LocalDateTime.now().plusYears(1));

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(bucketService.getBucketsDueAfter(eq(user.getId()), any(LocalDate.class)))
			.thenReturn(Collections.singletonList(bucket));
		when(myDataService.getTotalAsset(user.getId())).thenReturn(totalAssets);

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertTrue(result.isRecommended(), "Recommendation should be needed");
		assertEquals(expectedTemplate, result.getInvestmentType(), "Investment type should match");
		assertTrue(result.getEstimatedInvestRatio().compareTo(monthlyInvestmentRatio) >= 0,
			"Estimated invest ratio should be greater than or equal to the expected ratio");

		System.out.println("Current Template: " + currentTemplate.getName());
		System.out.println("Expected Template: " + expectedTemplate);
		System.out.println("Actual Template: " + result.getInvestmentType());
		System.out.println("Expected Ratio: " + monthlyInvestmentRatio);
		System.out.println("Actual Ratio: " + result.getEstimatedInvestRatio());
	}

	@Test
	void recommendGoalPortfolio_ConservativeTemplate() {
		testRecommendation(BigDecimal.valueOf(0.01), "CONSERVATIVE");
	}

	@Test
	void recommendGoalPortfolio_ModeratelyConservativeTemplate() {
		testRecommendation(BigDecimal.valueOf(0.05), "MODERATELY_CONSERVATIVE");
	}

	@Test
	void recommendGoalPortfolio_BalancedTemplate() {
		testRecommendation(BigDecimal.valueOf(0.10), "BALANCED");
	}

	@Test
	void recommendGoalPortfolio_ModeratelyAggressiveTemplate() {
		testRecommendation(BigDecimal.valueOf(0.20), "MODERATELY_AGGRESSIVE");
	}

	@Test
	void recommendGoalPortfolio_AggressiveTemplate() {
		testRecommendation(BigDecimal.valueOf(0.35), "AGGRESSIVE");
	}

	@Test
	void recommendGoalPortfolio_NoRecommendationNeeded() {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolio currentGoal = new GoalPortfolio();
		PortfolioTemplate currentTemplate = new PortfolioTemplate();
		currentTemplate.setName("AGGRESSIVE");
		currentGoal.setTemplate(currentTemplate);

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertFalse(result.isRecommended());
	}

	@Test
	void recommendGoalPortfolio_UserNotFound() {
		User user = new User();
		user.setId("nonExistentUser");
		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
			() -> goalPortfolioService.recommendGoalPortfolio(user));
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오를 찾을 수 없습니다.", exception.getMessage());
	}

	@Test
	void recommendGoalPortfolio_SafeBoxHandling() {
		User user = new User();
		user.setId("testUser");
		GoalPortfolio currentGoal = new GoalPortfolio();
		currentGoal.setTemplate(new PortfolioTemplate());
		currentGoal.getTemplate().setName("BALANCED");

		Bucket bucketWithSafeBox = new Bucket();
		bucketWithSafeBox.setGoalAmount(BigDecimal.valueOf(10000));
		bucketWithSafeBox.setSafeBox(BigDecimal.valueOf(1000));
		bucketWithSafeBox.setDueDate(LocalDateTime.now().plusYears(2));

		Bucket bucketWithoutSafeBox = new Bucket();
		bucketWithoutSafeBox.setGoalAmount(BigDecimal.valueOf(10000));
		bucketWithoutSafeBox.setDueDate(LocalDateTime.now().plusYears(2));

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(bucketService.getBucketsDueAfter(eq(user.getId()), any(LocalDate.class)))
			.thenReturn(Arrays.asList(bucketWithSafeBox, bucketWithoutSafeBox));
		when(myDataService.getTotalAsset(user.getId())).thenReturn(BigDecimal.valueOf(100000));

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertNotNull(result);
	}

	@Test
	void recommendGoalPortfolio_NullDueDateHandling() {
		User user = new User();
		user.setId("testUser");
		GoalPortfolio currentGoal = new GoalPortfolio();
		currentGoal.setTemplate(new PortfolioTemplate());
		currentGoal.getTemplate().setName("BALANCED");

		Bucket bucketWithNullDueDate = new Bucket();
		bucketWithNullDueDate.setGoalAmount(BigDecimal.valueOf(10000));
		bucketWithNullDueDate.setDueDate(null);

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(bucketService.getBucketsDueAfter(eq(user.getId()), any(LocalDate.class)))
			.thenReturn(Collections.singletonList(bucketWithNullDueDate));
		when(myDataService.getTotalAsset(user.getId())).thenReturn(BigDecimal.valueOf(100000));

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertNotNull(result);
	}

	@Test
	void recommendGoalPortfolio_ZeroDaysToAchieve() {
		User user = new User();
		user.setId("testUser");
		GoalPortfolio currentGoal = new GoalPortfolio();
		currentGoal.setTemplate(new PortfolioTemplate());
		currentGoal.getTemplate().setName("BALANCED");

		Bucket bucketWithZeroDays = new Bucket();
		bucketWithZeroDays.setGoalAmount(BigDecimal.valueOf(10000));
		bucketWithZeroDays.setDueDate(LocalDateTime.now());

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.of(currentGoal));
		when(bucketService.getBucketsDueAfter(eq(user.getId()), any(LocalDate.class)))
			.thenReturn(Collections.singletonList(bucketWithZeroDays));
		when(myDataService.getTotalAsset(user.getId())).thenReturn(BigDecimal.valueOf(100000));

		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo result = goalPortfolioService.recommendGoalPortfolio(user);

		assertNotNull(result);
	}

	@Test
	void applyRecommendation_UserGoalPortfolioNotFound() {
		User user = new User();
		user.setId("testUserId");

		GoalPortfolioRequestDto.Customize request = GoalPortfolioRequestDto.Customize.builder()
			.customizedInvestmentType("AGGRESSIVE")
			.build();

		when(goalPortfolioRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () ->
			goalPortfolioService.applyRecommendation(request, user)
		);

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 목표 포트폴리오를 찾을 수 없습니다.", exception.getMessage());

		verify(goalPortfolioRepository).findByUserId(user.getId());
		verifyNoInteractions(portfolioTemplateService);
	}
}
