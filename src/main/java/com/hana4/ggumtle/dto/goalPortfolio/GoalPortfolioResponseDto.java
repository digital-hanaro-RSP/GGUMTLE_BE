package com.hana4.ggumtle.dto.goalPortfolio;

import java.math.BigDecimal;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class GoalPortfolioResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class Ratio extends BaseDto {
		private BigDecimal depositWithdrawalRatio;
		private BigDecimal savingTimeDepositRatio;
		private BigDecimal investmentRatio;
		private BigDecimal foreignCurrencyRatio;
		private BigDecimal pensionRatio;
		private BigDecimal etcRatio;
		private long id;
		private String userId;

		public static Ratio from(GoalPortfolio goalPortfolio) {
			return Ratio.builder()
				.depositWithdrawalRatio(goalPortfolio.getDepositWithdrawalRatio())
				.savingTimeDepositRatio(goalPortfolio.getSavingTimeDepositRatio())
				.investmentRatio(goalPortfolio.getInvestmentRatio())
				.foreignCurrencyRatio(goalPortfolio.getForeignCurrencyRatio())
				.pensionRatio(goalPortfolio.getPensionRatio())
				.etcRatio(goalPortfolio.getEtcRatio())
				.id(goalPortfolio.getId())
				.userId(goalPortfolio.getUser().getId())
				.createdAt(goalPortfolio.getCreatedAt())
				.updatedAt(goalPortfolio.getUpdatedAt())
				.build();
		}
	}
}
