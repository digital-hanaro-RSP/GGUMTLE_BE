package com.hana4.ggumtle.controller;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.bucketlist.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketlist.BucketResponseDto;
import com.hana4.ggumtle.service.BucketService;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {

    @Autowired
    private BucketService bucketService;

    @PostMapping
    public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> createBucket(@RequestBody @Valid BucketRequestDto requestDto) {

        BucketResponseDto.BucketInfo createdBucket = bucketService.createBucket(requestDto);

        return ResponseEntity.ok(ApiResponse.success(createdBucket));
    }
    @PutMapping("/{bucketId}")
    public ResponseEntity<ApiResponse<BucketResponseDto.BucketInfo>> updateBucket(@PathVariable Long bucketId, @RequestBody BucketRequestDto requestDto) {
        // 1. Bucket을 수정하고 반환
        BucketResponseDto.BucketInfo updatedBucket = bucketService.updateBucket(bucketId, requestDto);

        // 2. 수정된 Bucket을 응답으로 반환
        return ResponseEntity.ok(ApiResponse.success(updatedBucket));
    }

}
