package com.hana4.ggumtle.dto.MainPortfolio;

import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.dto.myData.MyDataResponseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class MainPortfolioResponseDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class PortfolioInfo {
		private MyDataResponseDto.CurrentPortfolio currentPortfolio;
		private GoalPortfolioResponseDto.Ratio goalPortfolio;

		public static PortfolioInfo from(MyDataResponseDto.CurrentPortfolio currentPortfolio,
			GoalPortfolioResponseDto.Ratio goalPortfolio) {
			return PortfolioInfo.builder()
				.currentPortfolio(currentPortfolio)
				.goalPortfolio(goalPortfolio)
				.build();
		}
	}
}
