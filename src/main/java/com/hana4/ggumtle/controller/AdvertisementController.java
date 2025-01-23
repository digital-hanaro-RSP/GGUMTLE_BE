package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.advertisement.AdvertisementResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.AdvertisementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
@Tag(name = "Advertisement", description = "메인페이지, 커뮤니티 광고 조회 API")
public class AdvertisementController {
	public final AdvertisementService advertisementService;

	@Operation(
		summary = "사용자 메인 광고 조회",
		description = "사용자의 투자 성향에 따라 메인 광고를 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "광고 조회 성공"),
		@ApiResponse(
			responseCode = "404",
			description = "해당 유저의 투자 성향 또는 광고를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 위험 등급의 광고가 존재하지 않습니다.\" }"
			))
		),
		@ApiResponse(
			responseCode = "500",
			description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"지원하지 않는 투자성향 타입입니다.\" }"
			))
		)
	})
	@GetMapping("/main")
	public ResponseEntity<CustomApiResponse<AdvertisementResponseDto.MainAd>> getMainAd(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		AdvertisementResponseDto.MainAd response = advertisementService.getMainAd(userDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success(response));
	}
}
