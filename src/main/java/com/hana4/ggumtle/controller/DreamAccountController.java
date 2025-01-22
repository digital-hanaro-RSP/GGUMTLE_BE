package com.hana4.ggumtle.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountRequestDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
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
	@PostMapping("/create")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> createDreamAccount(
		@RequestBody @Valid DreamAccountRequestDto.Create requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			// DreamAccount 생성
			DreamAccount newDreamAccount = dreamAccountService.createDreamAccount(requestDto, userDetails.getUser());

			// 응답 데이터 생성
			DreamAccountResponseDto.DreamAccountInfo responseDto = DreamAccountResponseDto.DreamAccountInfo.builder()
				.id(newDreamAccount.getId())
				.userId(newDreamAccount.getUser().getId())
				.balance(newDreamAccount.getBalance())
				.total(newDreamAccount.getTotal())
				.build();

			return ResponseEntity.status(HttpStatus.CREATED)
				.body(CustomApiResponse.success(responseDto));
		} catch (RuntimeException e) {
			// 에러 응답
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(CustomApiResponse.failure(400, "Failed to create Dream Account.", e.getMessage()));
		}
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
