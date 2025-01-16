package com.hana4.ggumtle.dto.survey;

import java.util.List;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.survey.Survey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Generated
public class SurveyResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@SuperBuilder
	public static class CreateResponse extends BaseDto {
		Long id;
		String userId;
		List<Integer> answers;

		public static CreateResponse from(Survey survey) {
			return CreateResponse.builder()
				.id(survey.getId())
				.userId(survey.getUser().getId())
				.answers(survey.getAnswers())
				.createdAt(survey.getCreatedAt())
				.updatedAt(survey.getUpdatedAt())
				.build();
		}
	}
}
