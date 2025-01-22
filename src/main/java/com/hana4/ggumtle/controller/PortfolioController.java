package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.MainPortfolio.MainPortfolioResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.MainPortfolioService;

import io.swagger.v3.oas.annotations.Operation;
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

}
