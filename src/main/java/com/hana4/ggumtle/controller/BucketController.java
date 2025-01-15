package com.hana4.ggumtle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.BucketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {

	@Autowired
	private BucketService bucketService;

	@PostMapping
	public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> createBucket(
		@RequestBody @Valid BucketRequestDto.Create requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		BucketResponseDto.BucketInfo createdBucket = bucketService.createBucket(requestDto, userDetails.getUser());

		return ResponseEntity.ok(ApiResponse.success(createdBucket));
	}

	@PutMapping("/{bucketId}")
	public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> updateBucket(
		@PathVariable("bucketId") Long bucketId,
		@RequestBody BucketRequestDto.Create requestDto) {
		// 1. Bucket을 수정하고 반환
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucket(bucketId, requestDto);

		// 2. 수정된 Bucket을 응답으로 반환
		return ResponseEntity.ok(ApiResponse.success(updatedBucket));
	}

	@PatchMapping("/{bucketId}")
	public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> updateBucketStatus(
		@PathVariable("bucketId") Long bucketId,
		@RequestBody Map<String, String> updates) {
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucketStatus(bucketId, updates);

		return ResponseEntity.ok(ApiResponse.success(updatedBucket));
	}

	@DeleteMapping("/{bucketId}")
	public ResponseEntity<ApiResponse<Void>> deleteBucket(@PathVariable("bucketId") Long bucketId) {
		bucketService.deleteBucket(bucketId);
		return ResponseEntity.ok(ApiResponse.success(null)); // 204 No Content 반환
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<BucketResponseDto.BucketInfo>>> getAllBuckets() {
		List<BucketResponseDto.BucketInfo> allBuckets = bucketService.getAllBuckets();
		return ResponseEntity.ok(ApiResponse.success(allBuckets)); // 200 OK와 함께 리스트 반환
	}

	@GetMapping("/{bucketId}")
	public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> getBucketById(
		@PathVariable("bucketId") Long bucketId) {
		BucketResponseDto.BucketInfo bucketInfo = bucketService.getBucketById(bucketId);
		return ResponseEntity.ok(ApiResponse.success(bucketInfo)); // 200 OK와 함께 버킷 반환
	}

	@GetMapping("/recommendation")
	public ResponseEntity<ApiResponse<List<RecommendationResponseDto.RecommendedBucketInfo>>> getRecommendedBuckets() {
		List<RecommendationResponseDto.RecommendedBucketInfo> recommendations = bucketService.getRecommendedBuckets();
		return ResponseEntity.ok(ApiResponse.success(recommendations));
	}
}
