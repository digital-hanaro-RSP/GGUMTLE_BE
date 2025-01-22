package com.hana4.ggumtle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountRequestDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.DreamAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dreamAccount")
@Tag(name = "DreamAccount", description = "꿈통장 관련 API")

public class DreamAccountController {

	private final DreamAccountService dreamAccountService;

	// 꿈통장 생성
	@PostMapping
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> createDreamAccount(
		@RequestBody @Valid DreamAccountRequestDto.Create requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			// DreamAccount 생성
			DreamAccountResponseDto.DreamAccountInfo dreamAccount = dreamAccountService.createDreamAccount(requestDto,
				userDetails.getUser());
			
			return ResponseEntity.status(HttpStatus.CREATED)
				.body(CustomApiResponse.success(dreamAccount));
		} catch (RuntimeException e) {
			// 에러 응답
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(CustomApiResponse.failure(400, "Failed to create Dream Account.", e.getMessage()));
		}
	}

	@Operation(summary = "유저가 가진 꿈통장", description = "유저가 가진 꿈통장을 반환합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@GetMapping
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> getDreamAccount(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		// 사용자의 ID로 DreamAccount를 검색
		DreamAccountResponseDto.DreamAccountInfo dreamAccount = dreamAccountService.getDreamAccountByUserId(
			userDetails.getUser().getId());

		// 검색된 DreamAccount를 응답으로 반환
		return ResponseEntity.ok(CustomApiResponse.success(dreamAccount));
	}

	//꿈통장 금액 추가
	@Operation(summary = "꿈통장에 금액 추가", description = "꿈통장에 돈을 추가합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@PostMapping("/{dreamAccountId}/amounts")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> addAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@RequestBody @Valid DreamAccountRequestDto.AddAmount requestDto) {

		DreamAccountResponseDto.DreamAccountInfo addedDreamAccount = dreamAccountService.addAmountToDreamAccount(
			dreamAccountId, requestDto.getAmount());

		return ResponseEntity.ok(CustomApiResponse.success(addedDreamAccount));
	}

	// 꿈통장에서 금액 제외
	@Operation(summary = "꿈통장에서 금액 제거", description = "꿈통장에서 돈을 뺍니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@DeleteMapping("/{dreamAccountId}/amounts")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> subtractAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@RequestBody @Valid DreamAccountRequestDto.AddAmount requestDto) {

		DreamAccountResponseDto.DreamAccountInfo subtractedDreamAccount = dreamAccountService.subtractAmountFromDreamAccount(
			dreamAccountId, requestDto.getAmount());
		return ResponseEntity.ok(CustomApiResponse.success(subtractedDreamAccount));
	}

	//꿈통장 금액을 Bucket의 safeBox로 분배
	@Operation(summary = "꿈통장에서 safebox로 돈 분배", description = "꿈통장에서 버킷에 돈을 분배합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@PostMapping("/{dreamAccountId}/buckets/{bucketId}/distributions")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> distributeAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@PathVariable("bucketId") Long bucketId,
		@RequestBody @Valid DreamAccountRequestDto.DistributeAmount requestDto) {

		// DreamAccount와 연관된 Bucket으로 금액 분배
		DreamAccountResponseDto.DreamAccountInfo updatedDreamAccount = dreamAccountService.distributeAmountToBucket(
			dreamAccountId, bucketId, requestDto.getAmount());

		return ResponseEntity.ok(CustomApiResponse.success(updatedDreamAccount));
	}

	@Operation(summary = "버킷에서 꿈통장으로 보내기", description = "버킷의 safebox에서 꿈통장으로 돈을 보냅니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@DeleteMapping("/{dreamAccountId}/buckets/{bucketId}/distributions")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> emptyAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@PathVariable("bucketId") Long bucketId,
		@RequestBody @Valid DreamAccountRequestDto.DistributeAmount requestDto) {

		// DreamAccount와 연관된 Bucket으로 금액 분배
		DreamAccountResponseDto.DreamAccountInfo updatedDreamAccount = dreamAccountService.distributeAmountToDreamAccount(
			dreamAccountId, bucketId, requestDto.getAmount());

		return ResponseEntity.ok(CustomApiResponse.success(updatedDreamAccount));
	}
}
