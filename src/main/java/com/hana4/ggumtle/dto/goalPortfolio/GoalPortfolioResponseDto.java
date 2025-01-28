package com.hana4.ggumtle.dto.goalPortfolio;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;

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
		@Schema(description = "입출금 비율", example = "0")
		private BigDecimal depositWithdrawalRatio;
		@Schema(description = "예적금 비율", example = "0.70")
		private BigDecimal savingTimeDepositRatio;
		@Schema(description = "투자 비율", example = "0.20")
		private BigDecimal investmentRatio;
		@Schema(description = "외화 비율", example = "0")
		private BigDecimal foreignCurrencyRatio;
		@Schema(description = "연금 비율", example = "0.10")
		private BigDecimal pensionRatio;
		@Schema(description = "기타 비율", example = "0")
		private BigDecimal etcRatio;
		@Schema(description = "포트폴리오 ID", example = "1")
		private long id;
		@Schema(description = "사용자 ID", example = "be4aa756-db01-4594-b83b-4132a94febd7")
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
				.investmentType(
					goalPortfolio.getCustomizedTemplate() != null ? goalPortfolio.getCustomizedTemplate().getName() :
						goalPortfolio.getTemplate().getName())
				.userName(userName)
				.build();
		}
	}

	@Schema(description = "목표 포트폴리오 템플릿 목록 반환 DTO")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@Builder
	public static class GoalTemplateOption {
		private List<Template> templates;

		public static GoalTemplateOption from(List<PortfolioTemplate> portfolioTemplates) {
			List<Template> templates = portfolioTemplates.stream()
				.map(Template::from)
				.collect(Collectors.toList());

			return GoalTemplateOption.builder()
				.templates(templates)
				.build();
		}
	}

	@Schema(description = "목표 포트폴리오 템플릿 반환 DTO")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@Builder
	public static class Template {
		@Schema(description = "id", example = "1")
		private long id;
		@Schema(description = "템플릿명", example = "CONSERVATIVE")
		private String name;
		@Schema(description = "입출금 비율", example = "0")
		private BigDecimal depositWithdrawalRatio;
		@Schema(description = "예적금 비율", example = "0.70")
		private BigDecimal savingTimeDepositRatio;
		@Schema(description = "투자 비율", example = "0.20")
		private BigDecimal investmentRatio;
		@Schema(description = "외화 비율", example = "0")
		private BigDecimal foreignCurrencyRatio;
		@Schema(description = "연금 비율", example = "0.10")
		private BigDecimal pensionRatio;
		@Schema(description = "기타 비율", example = "0")
		private BigDecimal etcRatio;

		public static Template from(PortfolioTemplate portfolioTemplate) {
			return Template.builder()
				.id(portfolioTemplate.getId())
				.name(portfolioTemplate.getName())
				.depositWithdrawalRatio(portfolioTemplate.getDepositWithdrawalRatio())
				.savingTimeDepositRatio(portfolioTemplate.getSavingTimeDepositRatio())
				.investmentRatio(portfolioTemplate.getInvestmentRatio())
				.foreignCurrencyRatio(portfolioTemplate.getForeignCurrencyRatio())
				.pensionRatio(portfolioTemplate.getPensionRatio())
				.etcRatio(portfolioTemplate.getEtcRatio())
				.build();
		}
	}

	@Schema(description = "추천 목표 포트폴리오 반환 DTO")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@Builder
	public static class RecommendGoalPortfolioInfo {
		@Schema(description = "추천된 포트폴리오가 있는지 여부", example = "true")
		private boolean isRecommended;
		@Schema(description = "CONSERVATIVE(안정형), MODERATELY_CONSERVATIVE(안정추구형), BALANCED(위험중립형), MODERATELY_AGGRESSIVE(적극투자형), AGGRESSIVE(공격투자형)", example = "BALANCED")
		private String investmentType;
		@Schema(description = "자산 대비 꿈에 투자해야할 월 투자 비율", example = "0.12")
		private BigDecimal estimatedInvestRatio;

		public static RecommendGoalPortfolioInfo from(
			boolean isRecommended
		) {
			return RecommendGoalPortfolioInfo.builder()
				.isRecommended(isRecommended)
				.build();
		}

		public static RecommendGoalPortfolioInfo from(
			boolean isRecommended,
			String investmentType,
			BigDecimal estimatedInvestRatio
		) {
			return RecommendGoalPortfolioInfo.builder()
				.isRecommended(isRecommended)
				.investmentType(investmentType)
				.estimatedInvestRatio(estimatedInvestRatio)
				.build();
		}
	}
}
