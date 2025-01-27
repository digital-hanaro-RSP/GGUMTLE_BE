package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.MainPortfolio.MainPortfolioResponseDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioRequestDto;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.GoalPortfolioService;
import com.hana4.ggumtle.service.MainPortfolioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portfolio")
@Tag(name = "Portfolio", description = "포트폴리오 조회 API")
public class PortfolioController {

	private final MainPortfolioService mainPortfolioService;
	private final GoalPortfolioService goalPortfolioService;

	@Operation(summary = "메인페이지 포트폴리오 조회", description = "메인페이지에서 유저의 현재 포트폴리오와 목표포트폴리오를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@GetMapping
	public ResponseEntity<CustomApiResponse<MainPortfolioResponseDto.PortfolioInfo>> getGoalPortfolioByUserId(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		MainPortfolioResponseDto.PortfolioInfo mainPortfolio = mainPortfolioService.getMainPortfolioByUserId(
			userDetails.getUser().getId());
		return ResponseEntity.ok(CustomApiResponse.success(mainPortfolio));
	}

	@Operation(summary = "메인페이지 사용자 이름과 투자성향 조회", description = "이름과 투자성향을 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "이름과 투자성향 반환 성공"),
		@ApiResponse(responseCode = "404", description = "사용자의 목표 포트폴리오를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 유저의 목표 포트폴리오가 존재하지 않습니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@GetMapping("/investmentType")
	public ResponseEntity<CustomApiResponse<GoalPortfolioResponseDto.InvestmentType>> getUserNameAndInvestmentType(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		GoalPortfolioResponseDto.InvestmentType response = goalPortfolioService.getGoalPortfolioInvestmentTypeByUser(
			userDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success(response));
	}

	@Operation(summary = "선택할 수 있는 목표 포트폴리오들 반환", description = "목표 포트폴리오 템플릿들을 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "목표 포트폴리오들 반환 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@GetMapping("/templates")
	public ResponseEntity<CustomApiResponse<GoalPortfolioResponseDto.GoalTemplateOption>> getGoalPortfolioTemplate(
	) {
		GoalPortfolioResponseDto.GoalTemplateOption response = goalPortfolioService.getGoalPortfolioTemplate();
		return ResponseEntity.ok(CustomApiResponse.success(response));
	}

	// 추천 계산 반환 api
	@Operation(summary = "추천 목표 포트폴리오를 반환합니다.", description = "추천 목표 포트폴리오 반환")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "추천 목표 포트폴리오 반환 성공"),
		@ApiResponse(responseCode = "404", description = "사용자의 목표 포트폴리오를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 유저의 목표 포트폴리오를 찾을 수 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@GetMapping("/recommendation")
	public ResponseEntity<CustomApiResponse<GoalPortfolioResponseDto.RecommendGoalPortfolioInfo>> recommendGoalPortfolio(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		GoalPortfolioResponseDto.RecommendGoalPortfolioInfo response = goalPortfolioService.recommendGoalPortfolio(
			userDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success(response));
	}

	// 추천 포트폴리오 적용 api
	@Operation(summary = "사용자가 목표 포트폴리오를 직접 설정", description = "사용자가 목표 포트폴리오를 수정할 수 있는 api입니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 설정 목표 포트폴리오로 수정 성공"),
		@ApiResponse(responseCode = "404", description = "사용자의 목표 포트폴리오를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 유저의 목표 포트폴리오를 찾을 수 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PatchMapping("/recommendation")
	public ResponseEntity<CustomApiResponse<GoalPortfolioResponseDto.Ratio>> applyRecommendation(
		@RequestBody GoalPortfolioRequestDto.Customize request, @AuthenticationPrincipal CustomUserDetails userDetails
	) {
		GoalPortfolioResponseDto.Ratio response = goalPortfolioService.applyRecommendation(request,
			userDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success(response));
	}
}
