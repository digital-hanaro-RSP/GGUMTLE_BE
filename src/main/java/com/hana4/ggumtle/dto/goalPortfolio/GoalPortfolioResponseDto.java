package com.hana4.ggumtle.dto.goalPortfolio;

import java.math.BigDecimal;

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
	public static class Ratio {
		private BigDecimal depositWithdrawalRatio;
		private BigDecimal savingTimeDepositRatio;
		private BigDecimal investmentRatio;
		private BigDecimal foreignCurrencyRatio;
		private BigDecimal pensionRatio;
		private BigDecimal etcRatio;

		public static Ratio from(GoalPortfolio goalPortfolio) {
			return Ratio.builder()
				.depositWithdrawalRatio(goalPortfolio.getDepositWithdrawalRatio())
				.savingTimeDepositRatio(goalPortfolio.getSavingTimeDepositRatio())
				.investmentRatio(goalPortfolio.getInvestmentRatio())
				.foreignCurrencyRatio(goalPortfolio.getForeignCurrencyRatio())
				.pensionRatio(goalPortfolio.getPensionRatio())
				.etcRatio(goalPortfolio.getEtcRatio())
				.build();
		}
	}
}
