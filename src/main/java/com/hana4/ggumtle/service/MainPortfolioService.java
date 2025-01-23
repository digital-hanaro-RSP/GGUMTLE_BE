package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.MainPortfolio.MainPortfolioResponseDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto.Ratio;
import com.hana4.ggumtle.dto.myData.MyDataResponseDto.CurrentPortfolio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainPortfolioService {

	private final GoalPortfolioService goalPortfolioService;
	private final MyDataService myDataService;

	/**
	 * MainPortfolio 정보를 가져오는 서비스 메서드
	 *
	 * @param userId 사용자 ID
	 * @return PortfolioInfo
	 */
	public MainPortfolioResponseDto.PortfolioInfo getMainPortfolioByUserId(String userId) {
		// MyData 정보 조회
		CurrentPortfolio currentPortfolio = myDataService.getMyDataByUserId(userId);

		// GoalPortfolio 정보 조회
		Ratio goalPortfolio = goalPortfolioService.getGoalPortfolioByUserId(userId);

		// PortfolioInfo 생성 및 반환
		return MainPortfolioResponseDto.PortfolioInfo.from(currentPortfolio, goalPortfolio);
	}
}
