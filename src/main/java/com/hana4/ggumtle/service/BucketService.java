package com.hana4.ggumtle.service;

import com.hana4.ggumtle.dto.bucketlist.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketlist.BucketResponseDto;
import com.hana4.ggumtle.repository.BucketRepository;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BucketService {
	private final BucketRepository bucketRepository;


    public BucketResponseDto.BucketInfo createBucket(BucketRequestDto requestDto) {
        // 추천 플로우 유효성 검사
        if (Boolean.TRUE.equals(requestDto.getIsRecommended()) && requestDto.getOriginId() == null) {
            throw new IllegalArgumentException("추천 플로우에서는 originId가 필요합니다.");
        }

        Bucket bucket = bucketRepository.save(requestDto.toEntity());

        return BucketResponseDto.BucketInfo.form(bucket);
    }

    public BucketResponseDto.BucketInfo updateBucket(Long bucketId,BucketRequestDto requestDto) {
        Bucket bucket = bucketRepository.findById(bucketId);

        bucket.updateFromDto(requestDto);

        return BucketResponseDto.BucketInfo.form(bucket);
    }

	public Bucket getBucket(Long bucketId) {
		return bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 Bucket이 존재하지 않습니다."));
	}
}
