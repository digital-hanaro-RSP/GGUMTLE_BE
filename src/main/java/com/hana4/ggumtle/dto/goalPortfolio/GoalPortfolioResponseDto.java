package com.hana4.ggumtle.dto.goalPortfolio;

import java.math.BigDecimal;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Schema(description = "목표포트폴리오 관련 응답 DTO")
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

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@Builder
	@Schema(description = "메인페이지 사용자이름,투자성향 DTO")
	public static class InvestmentType {
		@Schema(description = "사용자 이름", example = "홍길동")
		private String userName;
		@Schema(description = "사용자 투자 성향", example = "CONSERVATIVE(안정형), MODERATELY_CONSERVATIVE(안정추구형), BALANCED(위험중립형), MODERATELY_AGGRESSIVE(적극투자형), AGGRESSIVE(공격투자형)")
		private String investmentType;

		public static InvestmentType from(GoalPortfolio goalPortfolio, String userName) {
			return InvestmentType.builder()
				.investmentType(goalPortfolio.getTemplate().getName())
				.userName(userName)
				.build();
		}
	}
}
