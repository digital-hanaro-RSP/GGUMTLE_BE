package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.survey.SurveyRequestDto;
import com.hana4.ggumtle.dto.survey.SurveyResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.survey.Survey;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.SurveyRepository;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

	@Mock
	private SurveyRepository surveyRepository;

	@Mock
	private GoalPortfolioService goalPortfolioService;

	@Mock
	private UserService userService;

	@Mock
	private PortfolioTemplateService portfolioTemplateService;

	@InjectMocks
	private SurveyService surveyService;

	private User user;
	private SurveyRequestDto.Create surveyRequestDto;
	private PortfolioTemplate portfolioTemplate;

	@BeforeEach
	void setUp() {
		user = new User(
			"8598f3cb-32b2-4b89-b9d7-570153affb51", // id
			"010-1234-5678", // tel
			"password123", // password
			"홍길동", // name
			(short)1, // permission
			LocalDateTime.of(1990, 1, 1, 0, 0, 0, 0), // birthDate
			"M", // gender
			UserRole.USER, // role
			"https://example.com/profile.jpg", // profileImageUrl
			"hgildong" // nickname
		);

		user.setPermission((short)1);

		// surveyRequestDto = new SurveyRequestDto.Create().toBuilder().build()
		surveyRequestDto = SurveyRequestDto.Create.builder()
			.answers(List.of(1, 2, 3, 4, 5))
			.investmentType("BALANCED")
			.build();

		portfolioTemplate = new PortfolioTemplate();
	}

	@Test
	void createSurvey_ValidUser_Success() {
		when(portfolioTemplateService.findByName(anyString())).thenReturn(portfolioTemplate);
		// Create a mock Survey with a valid User
		Survey mockSurvey = new Survey();
		mockSurvey.setUser(user);
		when(surveyRepository.save(any())).thenReturn(mockSurvey);

		SurveyResponseDto.CreateResponse response = surveyService.createSurvey(surveyRequestDto, user);

		assertNotNull(response);
		verify(goalPortfolioService).createGoalPortfolioAndSave(portfolioTemplate, user);
		verify(userService).updatePermission(user, (short)3);
	}

	@Test
	void createSurvey_InvalidUser_ThrowsException() {
		user.setPermission((short)3); // 퍼미션으로 인한 invalid user case
		Survey mockSurvey = new Survey();
		mockSurvey.setCreatedAt(LocalDateTime.now());
		when(surveyRepository.findByUser(user)).thenReturn(Optional.of(mockSurvey));

		assertThrows(CustomException.class, () -> surveyService.createSurvey(surveyRequestDto, user));
	}

	@Test
	void validateTarget_NoExistingSurvey_ReturnsTrue() {
		when(surveyRepository.findByUser(user)).thenReturn(Optional.empty());

		assertTrue(surveyService.validateTarget(user));
	}

	@Test
	void validateTarget_OldSurvey_ReturnsTrue() {
		Survey oldSurvey = new Survey();
		oldSurvey.setCreatedAt(LocalDateTime.now().minusMonths(7));
		when(surveyRepository.findByUser(user)).thenReturn(Optional.of(oldSurvey));

		assertTrue(surveyService.validateTarget(user));
	}

	@Test
	void validateTarget_RecentSurveyLowPermission_ReturnsTrue() {
		Survey recentSurvey = new Survey();
		recentSurvey.setCreatedAt(LocalDateTime.now().minusMonths(1));
		when(surveyRepository.findByUser(user)).thenReturn(Optional.of(recentSurvey));

		assertTrue(surveyService.validateTarget(user));
	}

	@Test
	void validateTarget_RecentSurveyHighPermission_ReturnsFalse() {
		user.setPermission((short)3);
		Survey recentSurvey = new Survey();
		recentSurvey.setCreatedAt(LocalDateTime.now().minusMonths(1));
		when(surveyRepository.findByUser(user)).thenReturn(Optional.of(recentSurvey));

		assertFalse(surveyService.validateTarget(user));
	}
}
