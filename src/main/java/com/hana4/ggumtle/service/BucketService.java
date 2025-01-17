package com.hana4.ggumtle.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
import com.hana4.ggumtle.dto.bucketlist.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketlist.BucketResponseDto;
import com.hana4.ggumtle.repository.BucketRepository;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.BucketRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BucketService {

	private BucketRepository bucketRepository;

	@Autowired
	public BucketService(BucketRepository bucketRepository) {
		this.bucketRepository = bucketRepository;
	}

	public BucketResponseDto.BucketInfo createBucket(BucketRequestDto.Create requestDto, User user) {
		if (Boolean.TRUE.equals(requestDto.getIsRecommended()) && requestDto.getOriginId() == null) {
			throw new IllegalArgumentException("추천 플로우에서는 originId가 필요합니다.");
		}

		Bucket bucket = bucketRepository.save(requestDto.toEntity(user));

		return BucketResponseDto.BucketInfo.form(bucket);
	}

	public BucketResponseDto.BucketInfo updateBucket(Long bucketId, BucketRequestDto.Create requestDto) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new IllegalArgumentException("버킷을 찾을 수 없습니다."));

		bucket.updateFromDto(requestDto);

		return BucketResponseDto.BucketInfo.form(bucket);
	}

	public BucketResponseDto.BucketInfo updateBucketStatus(Long bucketId, Map<String, String> updates) {
		String statusValue = updates.get("status");
		if (statusValue == null || statusValue.isBlank()) {
			throw new IllegalArgumentException("Status 값은 필수입니다.");
		}

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new IllegalArgumentException("버킷을 찾을 수 없습니다."));

		BucketStatus newStatus = BucketStatus.valueOf(statusValue.toUpperCase()); // Enum 변환
		bucket.setStatus(newStatus);

		bucketRepository.save(bucket);
		return BucketResponseDto.BucketInfo.form(bucket);

	}

	public void deleteBucket(Long bucketId) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new IllegalArgumentException("버킷을 찾을 수 없습니다."));

		bucketRepository.delete(bucket);
		System.out.println("버킷이 삭제되었습니다");

		BucketResponseDto.BucketInfo.form(bucket);
	}

	public List<BucketResponseDto.BucketInfo> getAllBuckets() {
		List<Bucket> buckets = bucketRepository.findAll();

		return buckets.stream()
			.map(BucketResponseDto.BucketInfo::form)
			.collect(Collectors.toList());
	}

	public BucketResponseDto.BucketInfo getBucketById(Long bucketId) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new IllegalArgumentException("버킷을 찾을 수 없습니다."));

		return BucketResponseDto.BucketInfo.form(bucket);
	}

	public List<RecommendationResponseDto.RecommendedBucketInfo> getRecommendedBuckets() {
		// 버킷을 태그 타입별로 그룹화
		Map<BucketTagType, List<Bucket>> groupedBuckets = bucketRepository.findAll()
			.stream()
			.collect(Collectors.groupingBy(Bucket::getTagType));

		return groupedBuckets.entrySet()
			.stream()
			.map(entry -> {
				List<RecommendationResponseDto.RecommendedBucketInfo.Recommendation> topRecommendations = entry.getValue()
					.stream()
					.sorted((b1, b2) -> Long.compare(b2.getFollowers(), b1.getFollowers())) // followers 기준 내림차순 정렬
					.limit(3) // 상위 3개 선택
					.map(bucket -> RecommendationResponseDto.RecommendedBucketInfo.Recommendation.builder()
						.id(bucket.getId())
						.title(bucket.getTitle())
						.followers(bucket.getFollowers())
						.build())
					.toList();

				return RecommendationResponseDto.RecommendedBucketInfo.builder()
					.tagType(entry.getKey()) // 태그 타입
					.recommendations(topRecommendations) // 추천 리스트
					.build();
			})
			.toList();
	}

	public Bucket getBucket(Long bucketId) {
		return bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 Bucket이 존재하지 않습니다."));
	}
}
