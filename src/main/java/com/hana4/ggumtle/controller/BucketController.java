package com.hana4.ggumtle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.BucketService;
import com.hana4.ggumtle.service.DreamAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/buckets")
@RequiredArgsConstructor
@Tag(name = "Bucket", description = "생성, 수정, 상태변환, 삭제, 전체조회, 상세조회, 추천 API")
public class BucketController {

	private final BucketService bucketService;
	private final DreamAccountService dreamAccountService;

	@Operation(summary = "버킷리스트 생성", description = "새로운 버킷리스트를 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@PostMapping
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> createBucket(
		@RequestBody @Valid BucketRequestDto.CreateBucket requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		// Bucket 생성 로직 호출
		BucketResponseDto.BucketInfo createdBucket = bucketService.createBucket(requestDto, userDetails.getUser());

		return ResponseEntity.ok(CustomApiResponse.success(createdBucket));
	}

	@Operation(summary = "버킷리스트 수정", description = "버킷리스트를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@PutMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> updateBucket(
		@PathVariable("bucketId") Long bucketId,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid BucketRequestDto.CreateBucket requestDto) {
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucket(userDetails.getUser(), bucketId,
			requestDto);

		return ResponseEntity.ok(CustomApiResponse.success(updatedBucket));
	}

	@Operation(summary = "버킷리스트 상태변환", description = "버킷리스트 상태를 변환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@PatchMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> updateBucketStatus(
		@PathVariable("bucketId") Long bucketId,
		@AuthenticationPrincipal CustomUserDetails userDetails,

		@RequestBody @Valid BucketRequestDto.UpdateBucketStatus updates) {
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucketStatus(userDetails.getUser(), bucketId,
			updates);

		return ResponseEntity.ok(CustomApiResponse.success(updatedBucket));
	}

	@Operation(summary = "버킷리스트 삭제", description = "버킷리스트를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@DeleteMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<Void>> deleteBucket(@PathVariable("bucketId") Long bucketId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		bucketService.deleteBucket(bucketId, userDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success(null));
	}

	@Operation(summary = "버킷리스트 전체 조회", description = "버킷리스트를 전체 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@GetMapping
	public ResponseEntity<CustomApiResponse<List<BucketResponseDto.BucketInfo>>> getAllBuckets(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<BucketResponseDto.BucketInfo> allBuckets = bucketService.getAllBuckets(userDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success(allBuckets));
	}

	@Operation(summary = "버킷리스트 상세조회", description = "버킷리스트를 상세 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	@GetMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> getBucketById(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable("bucketId") Long bucketId) {
		BucketResponseDto.BucketInfo bucketInfo = bucketService.getBucketById(bucketId);
		return ResponseEntity.ok(CustomApiResponse.success(bucketInfo));
	}

	@Operation(summary = "버킷리스트 추천", description = "버킷리스트를 추천합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답")
	})
	// @GetMapping("/recommendation")
	// public ResponseEntity<CustomApiResponse<List<RecommendationResponseDto.RecommendedBucketInfo>>> getRecommendedBuckets() {
	// 	List<RecommendationResponseDto.RecommendedBucketInfo> recommendations = bucketService.getRecommendedBuckets();
	// 	return ResponseEntity.ok(CustomApiResponse.success(recommendations));
	// }

	@GetMapping("/recommendation")
	public ResponseEntity<CustomApiResponse<List<RecommendationResponseDto.RecommendedBucketInfo>>> getRecommendedBuckets(
		@RequestParam(required = false) BucketTagType tagType) {

		// tagType을 전달하여 추천된 버킷 리스트를 가져옴
		List<RecommendationResponseDto.RecommendedBucketInfo> recommendations = bucketService.getRecommendedBuckets(
			tagType);

		return ResponseEntity.ok(CustomApiResponse.success(recommendations));
	}
}
