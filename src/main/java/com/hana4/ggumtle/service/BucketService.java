package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.repository.BucketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BucketService {
	private final BucketRepository bucketRepository;

	public Bucket getBucket(Long bucketId) {
		return bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 Bucket이 존재하지 않습니다."));
	}
}
