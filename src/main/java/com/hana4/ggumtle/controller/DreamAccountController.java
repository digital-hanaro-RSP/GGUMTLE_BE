package com.hana4.ggumtle.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@RestController
@RequestMapping("/api/dreamAccount")
public class DreamAccountController {

	@Autowired
	private DreamAccountService dreamAccountService;

	//꿈통장 생성
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createDreamAccount(
		@RequestBody DreamAccountRequestDto.Create requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			// 기본값 설정 (initialAmount가 null일 경우 0으로 설정)
			// if (requestDto.getBalance() == null) {
			// 	requestDto.setBalance(BigDecimal.ZERO);
			// }
			// if (requestDto.getTotal() == null) {
			// 	requestDto.setTotal(BigDecimal.ZERO);
			// }

			System.out.println("User Id: " + requestDto.getUserId());
			System.out.println("Balance: " + requestDto.getBalance());
			System.out.println("Total: " + requestDto.getTotal());

			// DreamAccount 생성
			DreamAccount newDreamAccount = dreamAccountService.createDreamAccount(requestDto, userDetails.getUser());

			// 응답 데이터 생성
			Map<String, Object> response = new HashMap<>();
			response.put("code", 201);
			response.put("error", null);
			response.put("message", "Dream Account created successfully.");
			response.put("data", Map.of(
				"dreamAccountId", newDreamAccount.getId(),
				"userId", newDreamAccount.getUser().getId(),
				"balance", newDreamAccount.getBalance(),
				"total", newDreamAccount.getTotal()
			));

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (RuntimeException e) {
			// 에러 응답
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("code", 400);
			errorResponse.put("error", e.getMessage());
			errorResponse.put("message", "Failed to create Dream Account.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	//꿈통장 금액 추가
	@PostMapping("/add/{dreamAccountId}")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> addAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@RequestBody DreamAccountRequestDto.AddAmount requestDto) {

		DreamAccountResponseDto.DreamAccountInfo addedDreamAccount = dreamAccountService.addAmountToDreamAccount(
			dreamAccountId, requestDto.getAmount());

		return ResponseEntity.ok(CustomApiResponse.success(addedDreamAccount));
	}

	// 꿈통장에서 금액 제외
	@PostMapping("/subtract/{dreamAccountId}")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> subtractAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@RequestBody DreamAccountRequestDto.AddAmount requestDto) {

		DreamAccountResponseDto.DreamAccountInfo subtractedDreamAccount = dreamAccountService.subtractAmountFromDreamAccount(
			dreamAccountId, requestDto.getAmount());
		return ResponseEntity.ok(CustomApiResponse.success(subtractedDreamAccount));
	}

	//꿈통장 금액을 Bucket의 safeBox로 분배
	@PostMapping("/distribute/{dreamAccountId}/{bucketId}")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> distributeAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@PathVariable("bucketId") Long bucketId,
		@RequestBody DreamAccountRequestDto.DistributeAmount requestDto) {

		// DreamAccount와 연관된 Bucket으로 금액 분배
		DreamAccountResponseDto.DreamAccountInfo updatedDreamAccount = dreamAccountService.distributeAmountToBucket(
			dreamAccountId, bucketId, requestDto.getAmount());

		return ResponseEntity.ok(CustomApiResponse.success(updatedDreamAccount));
	}

	@PostMapping("/empty/{dreamAccountId}/{bucketId}")
	public ResponseEntity<CustomApiResponse<DreamAccountResponseDto.DreamAccountInfo>> emptyAmount(
		@PathVariable("dreamAccountId") Long dreamAccountId,
		@PathVariable("bucketId") Long bucketId,
		@RequestBody DreamAccountRequestDto.DistributeAmount requestDto) {

		// DreamAccount와 연관된 Bucket으로 금액 분배
		DreamAccountResponseDto.DreamAccountInfo updatedDreamAccount = dreamAccountService.distributeAmountToDreamAccount(
			dreamAccountId, bucketId, requestDto.getAmount());

		return ResponseEntity.ok(CustomApiResponse.success(updatedDreamAccount));
	}
}
