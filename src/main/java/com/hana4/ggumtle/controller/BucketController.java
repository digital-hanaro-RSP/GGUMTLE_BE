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

import com.hana4.ggumtle.dto.CustomApiResponse;
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
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> createBucket(
		@RequestBody @Valid BucketRequestDto.Create requestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		BucketResponseDto.BucketInfo createdBucket = bucketService.createBucket(requestDto, userDetails.getUser());

		return ResponseEntity.ok(CustomApiResponse.success(createdBucket));
	}

	@PutMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> updateBucket(
		@PathVariable("bucketId") Long bucketId,
		@RequestBody BucketRequestDto.Create requestDto) {
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucket(bucketId, requestDto);

		return ResponseEntity.ok(CustomApiResponse.success(updatedBucket));
	}

	@PatchMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> updateBucketStatus(
		@PathVariable("bucketId") Long bucketId,
		@RequestBody Map<String, String> updates) {
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucketStatus(bucketId, updates);

		return ResponseEntity.ok(CustomApiResponse.success(updatedBucket));
	}

	@DeleteMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<Void>> deleteBucket(@PathVariable("bucketId") Long bucketId) {
		bucketService.deleteBucket(bucketId);
		return ResponseEntity.ok(CustomApiResponse.success(null));
	}

	@GetMapping
	public ResponseEntity<CustomApiResponse<List<BucketResponseDto.BucketInfo>>> getAllBuckets() {
		List<BucketResponseDto.BucketInfo> allBuckets = bucketService.getAllBuckets();
		return ResponseEntity.ok(CustomApiResponse.success(allBuckets));
	}

	@GetMapping("/{bucketId}")
	public ResponseEntity<CustomApiResponse<BucketResponseDto.BucketInfo>> getBucketById(
		@PathVariable("bucketId") Long bucketId) {
		BucketResponseDto.BucketInfo bucketInfo = bucketService.getBucketById(bucketId);
		return ResponseEntity.ok(CustomApiResponse.success(bucketInfo));
	}

	@GetMapping("/recommendation")
	public ResponseEntity<CustomApiResponse<List<RecommendationResponseDto.RecommendedBucketInfo>>> getRecommendedBuckets() {
		List<RecommendationResponseDto.RecommendedBucketInfo> recommendations = bucketService.getRecommendedBuckets();
		return ResponseEntity.ok(CustomApiResponse.success(recommendations));
	}
}
