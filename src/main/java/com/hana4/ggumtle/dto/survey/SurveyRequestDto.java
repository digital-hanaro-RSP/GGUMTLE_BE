package com.hana4.ggumtle.dto.survey;

import java.util.List;

import com.hana4.ggumtle.model.entity.survey.Survey;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class SurveyRequestDto {
	@Schema(description = "투자성향설문 DTO")
	@Getter
	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class CreateSurvey {
		@Schema(description = "서베이 응답 답변 배열", example = "[1,2,3,4,5,4,3]", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "답변 배열을 입력하세요.")
		@NotEmpty(message = "답변 배열은 비어있을 수 없습니다.")
		private List<@NotNull(message = "답변은 null일 수 없습니다.") Integer> answers;

		@Schema(description = "투자 성향을 입력하세요", example = "CONSERVATIVE(안정형), MODERATELY_CONSERVATIVE(안정추구형), BALANCED // 위험중립형, MODERATELY_AGGRESSIVE // 적극투자형, AGGRESSIVE // 공격투자형", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "투자 성향을 입력하세요")
		private String investmentType;

		public Survey toEntity(User user) {
			return new Survey().toBuilder()
				.answers(this.answers)
				.user(user)
				.build();
		}
	}
}
