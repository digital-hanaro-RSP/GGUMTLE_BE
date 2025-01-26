package com.hana4.ggumtle.dto.goalPortfolio;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class GoalPortfolioRequestDto {
	@Schema(description = "목표 포트폴리오 커스텀 DTO")
	@Getter
	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Customize {
		@Schema(description = "커스텀할 투자 성향", example = "CONSERVATIVE(안정형), MODERATELY_CONSERVATIVE(안정추구형), BALANCED(위험중립형), MODERATELY_AGGRESSIVE(적극투자형), AGGRESSIVE(공격투자형)", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "커스텀할 투자 성향을 입력하세요")
		private String customizedInvestmentType;
	}
}
