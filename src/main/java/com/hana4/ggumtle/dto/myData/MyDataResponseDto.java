package com.hana4.ggumtle.dto.myData;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.hana4.ggumtle.model.entity.myData.MyData;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
@Schema(description = "마이데이터 응답 DTO")
public class MyDataResponseDto {

	@Schema(name = "CurrentPortfolioResponse", description = "현재 포트폴리오 정보 응답 DTO")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class CurrentPortfolio {
		@Schema(description = "입출금 금액", example = "1000000.00")
		private BigDecimal depositWithdrawal;

		@Schema(description = "적금 및 정기예금 금액", example = "5000000.00")
		private BigDecimal savingTimeDeposit;

		@Schema(description = "투자 금액", example = "3000000.00")
		private BigDecimal investment;

		@Schema(description = "외화 금액", example = "2000000.00")
		private BigDecimal foreignCurrency;

		@Schema(description = "연금 금액", example = "10000000.00")
		private BigDecimal pension;

		@Schema(description = "기타 금액", example = "500000.00")
		private BigDecimal etc;

		@Schema(description = "마이데이터 ID", example = "1")
		private long id;

		@Schema(description = "사용자 ID", example = "user123")
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

	@Schema(name = "CurrentPortfolioRateResponse", description = "현재 포트폴리오 비율 정보 응답 DTO")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class CurrentPortfolioRate {
		@Schema(description = "입출금 비율", example = "0.05")
		private BigDecimal depositWithdrawal;

		@Schema(description = "적금 및 정기예금 비율", example = "0.25")
		private BigDecimal savingTimeDeposit;

		@Schema(description = "투자 비율", example = "0.15")
		private BigDecimal investment;

		@Schema(description = "외화 비율", example = "0.10")
		private BigDecimal foreignCurrency;

		@Schema(description = "연금 비율", example = "0.40")
		private BigDecimal pension;

		@Schema(description = "기타 비율", example = "0.05")
		private BigDecimal etc;

		@Schema(description = "마이데이터 ID", example = "1")
		private long id;

		@Schema(description = "사용자 ID", example = "user123")
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
