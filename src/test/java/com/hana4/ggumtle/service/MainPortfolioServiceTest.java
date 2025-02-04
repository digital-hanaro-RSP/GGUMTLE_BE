package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.MainPortfolio.MainPortfolioResponseDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto.Ratio;
import com.hana4.ggumtle.dto.myData.MyDataResponseDto.CurrentPortfolio;

@ExtendWith(MockitoExtension.class)
class MainPortfolioServiceTest {

	@Mock
	private GoalPortfolioService goalPortfolioService;

	@Mock
	private MyDataService myDataService;

	@InjectMocks
	private MainPortfolioService mainPortfolioService;

	private String userId;
	private CurrentPortfolio mockCurrentPortfolio;
	private Ratio mockGoalPortfolio;

	@BeforeEach
	void setUp() {
		userId = "testUser";

		// Mock 데이터 설정
		mockCurrentPortfolio = mock(CurrentPortfolio.class);
		mockGoalPortfolio = mock(Ratio.class);

		when(myDataService.getMyDataByUserId(userId)).thenReturn(mockCurrentPortfolio);
		when(goalPortfolioService.getGoalPortfolioByUserId(userId)).thenReturn(mockGoalPortfolio);
	}

	@Test
	void testGetMainPortfolioByUserId() {
		// 실행
		MainPortfolioResponseDto.PortfolioInfo result = mainPortfolioService.getMainPortfolioByUserId(userId);

		// 검증
		assertNotNull(result);
		verify(myDataService, times(1)).getMyDataByUserId(userId);
		verify(goalPortfolioService, times(1)).getGoalPortfolioByUserId(userId);
	}
}
