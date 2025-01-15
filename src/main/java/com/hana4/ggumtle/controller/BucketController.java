package com.hana4.ggumtle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.bucketlist.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketlist.BucketResponseDto;
import com.hana4.ggumtle.service.BucketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {

	@Autowired
	private BucketService bucketService;

	@PostMapping
	public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> createBucket(
		@RequestBody @Valid BucketRequestDto requestDto) {

		BucketResponseDto.BucketInfo createdBucket = bucketService.createBucket(requestDto);

		return ResponseEntity.ok(ApiResponse.success(createdBucket));
	}

	@PutMapping("/{bucketId}")
	public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> updateBucket(
		@PathVariable("bucketId") Long bucketId,
		@RequestBody BucketRequestDto requestDto) {
		// 1. Bucket을 수정하고 반환
		BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucket(bucketId, requestDto);

		// 2. 수정된 Bucket을 응답으로 반환
		return ResponseEntity.ok(ApiResponse.success(updatedBucket));
	}

	@DeleteMapping("/{bucketId}")
	public ResponseEntity<ApiResponse<Void>> deleteBucket(@PathVariable("bucketId") Long bucketId) {
		bucketService.deleteBucket(bucketId);
		return ResponseEntity.ok(ApiResponse.success(null)); // 204 No Content 반환
	}

}
