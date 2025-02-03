package com.hana4.ggumtle.dto.myData;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
		private BigDecimal depositWithdrawal;
		private BigDecimal savingTimeDeposit;
		private BigDecimal investment;
		private BigDecimal foreignCurrency;
		private BigDecimal pension;
		private BigDecimal etc;
		private long id;
		private String userId;

		public static CurrentPortfolio from(MyData myData) {
			return CurrentPortfolio.builder()
				.depositWithdrawal(myData.getDepositWithdrawal())
				.savingTimeDeposit(myData.getSavingTimeDeposit())
				.investment(myData.getInvestment())
				.foreignCurrency(myData.getForeignCurrency())
				.pension(myData.getPension())
				.etc(myData.getEtc())
				.id(myData.getId())
				.userId(myData.getUser().getId())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class CurrentPortfolioRate {
		private BigDecimal depositWithdrawal;
		private BigDecimal savingTimeDeposit;
		private BigDecimal investment;
		private BigDecimal foreignCurrency;
		private BigDecimal pension;
		private BigDecimal etc;
		private long id;
		private String userId;

		public static CurrentPortfolioRate from(MyData myData) {
			BigDecimal sum = myData.getDepositWithdrawal().add(myData.getSavingTimeDeposit())
				.add(myData.getInvestment())
				.add(myData.getForeignCurrency())
				.add(myData.getPension())
				.add(myData.getEtc());
			return CurrentPortfolioRate.builder()
				.depositWithdrawal(myData.getDepositWithdrawal().divide(sum, 2, RoundingMode.HALF_UP))
				.savingTimeDeposit(myData.getSavingTimeDeposit().divide(sum, 2, RoundingMode.HALF_UP))
				.investment(myData.getInvestment().divide(sum, 2, RoundingMode.HALF_UP))
				.foreignCurrency(myData.getForeignCurrency().divide(sum, 2, RoundingMode.HALF_UP))
				.pension(myData.getPension().divide(sum, 2, RoundingMode.HALF_UP))
				.etc(myData.getEtc().divide(sum, 2, RoundingMode.HALF_UP))
				.id(myData.getId())
				.userId(myData.getUser().getId())
				.build();
		}
	}
}
