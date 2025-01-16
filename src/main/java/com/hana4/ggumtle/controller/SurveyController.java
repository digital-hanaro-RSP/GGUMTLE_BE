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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyController {
	public final SurveyService surveyService;

	@PostMapping
	public ResponseEntity<CustomApiResponse<SurveyResponseDto.CreateResponse>> createSurvey(
		@RequestBody SurveyRequestDto.Create request, @AuthenticationPrincipal CustomUserDetails userDetail) {
		return ResponseEntity.ok(CustomApiResponse.success(surveyService.createSurvey(request, userDetail.getUser())));
	}
}
