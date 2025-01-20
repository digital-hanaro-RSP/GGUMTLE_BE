package com.hana4.ggumtle.dto.survey;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.survey.Survey;
import com.hana4.ggumtle.model.entity.survey.SurveyType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Generated
public class SurveyResponseDto {
	@Schema(description = "투자성향설문 응답")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@SuperBuilder
	public static class CreateResponse extends BaseDto {
		@Schema(description = "id", example = "1")
		Long id;
		@Schema(description = "userId", example = "5b7de176-83f2-490a-b8d1-5ec4cbced4e8")
		String userId;
		@Schema(description = "answers", example = "INVESTMENT_RISK_TOLERANCE")
		SurveyType surveyType;

		public static CreateResponse from(Survey survey) {
			return CreateResponse.builder()
				.id(survey.getId())
				.userId(survey.getUser().getId())
				.surveyType(survey.getSurveyType())
				.createdAt(survey.getCreatedAt())
				.updatedAt(survey.getUpdatedAt())
				.build();
		}
	}
}
