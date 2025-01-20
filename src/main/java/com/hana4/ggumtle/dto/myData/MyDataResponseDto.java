package com.hana4.ggumtle.dto.myData;

import java.math.BigDecimal;

import com.hana4.ggumtle.model.entity.myData.MyData;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class MyDataResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class CurrentPortfolio {
		private BigDecimal depositWithdrawalRatio;
		private BigDecimal savingTimeDepositRatio;
		private BigDecimal investmentRatio;
		private BigDecimal foreignCurrencyRatio;
		private BigDecimal pensionRatio;
		private BigDecimal etcRatio;

		public static CurrentPortfolio from(MyData myData) {
			BigDecimal sum =
				myData.getDepositWithdrawal()
					.add(myData.getSavingTimeDeposit())
					.add(myData.getInvestment())
					.add(myData.getForeignCurrency())
					.add(myData.getPension())
					.add(myData.getEtc());

			return CurrentPortfolio.builder()
				.depositWithdrawalRatio(myData.getDepositWithdrawal().divide(sum, 4, BigDecimal.ROUND_HALF_UP))
				.savingTimeDepositRatio(myData.getSavingTimeDeposit().divide(sum, 4, BigDecimal.ROUND_HALF_UP))
				.investmentRatio(myData.getInvestment().divide(sum, 4, BigDecimal.ROUND_HALF_UP))
				.foreignCurrencyRatio(myData.getForeignCurrency().divide(sum, 4, BigDecimal.ROUND_HALF_UP))
				.pensionRatio(myData.getPension().divide(sum, 4, BigDecimal.ROUND_HALF_UP))
				.etcRatio(myData.getEtc().divide(sum, 4, BigDecimal.ROUND_HALF_UP))
				.build();
		}
	}
}
