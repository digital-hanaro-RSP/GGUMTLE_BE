package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.survey.SurveyRequestDto;
import com.hana4.ggumtle.dto.survey.SurveyResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.SurveyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Survey", description = "투자성향설문 API")
@RequestMapping("/survey")
public class SurveyController {
	public final SurveyService surveyService;

	@Operation(
		summary = "투자성향 설문 답변 저장",
		description = "투자성향 설문 답변, 설문 후 목표 포트폴리오 생성, user permission을 1X로 업데이트합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "답변 저장 성공"),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 400, \"error\": \"Bad Request\", \"message\": \"request 유효성 검사 실패 시.\" }"
			))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자입니다.\" }"
			))),
		@ApiResponse(responseCode = "409", description = "6개월 이내에 설문에 응한적이 있는 경우",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 409, \"error\": \"Conflict\", \"message\": \"서베이 대상자가 아닙니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PostMapping
	public ResponseEntity<CustomApiResponse<SurveyResponseDto.CreateResponse>> createSurvey(
		@RequestBody @Valid SurveyRequestDto.Create request, @AuthenticationPrincipal CustomUserDetails userDetail) {
		return ResponseEntity.ok(CustomApiResponse.success(surveyService.createSurvey(request, userDetail.getUser())));
	}
}
