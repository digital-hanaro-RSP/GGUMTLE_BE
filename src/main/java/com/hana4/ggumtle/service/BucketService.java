package com.hana4.ggumtle.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.BucketRepository;
import com.hana4.ggumtle.repository.DreamAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BucketService {

	private final BucketRepository bucketRepository;
	private final DreamAccountRepository dreamAccountRepository;

	public BucketResponseDto.BucketInfo createBucket(BucketRequestDto.CreateBucket requestDto, User user,
		DreamAccountResponseDto.DreamAccountInfo dreamAccountInfo) {
		if (Boolean.TRUE.equals(requestDto.getIsRecommended()) && requestDto.getOriginId() == null) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER, "추천 플로우에서는 originId가 필요합니다.");
		}
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountInfo.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "DreamAccount를 찾을 수 없습니다."));

		Bucket bucket = bucketRepository.save(requestDto.from(user, dreamAccount));

		return BucketResponseDto.BucketInfo.from(bucket);
	}

	public BucketResponseDto.BucketInfo updateBucket(Long bucketId, BucketRequestDto.CreateBucket requestDto) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));

		bucket.updateFromDto(requestDto);

		return BucketResponseDto.BucketInfo.from(bucket);
	}

	public BucketResponseDto.BucketInfo updateBucketStatus(Long bucketId, BucketRequestDto.UpdateBucketStatus updates) {
		BucketStatus statusValue = updates.getStatus();

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));

		// Enum 변환
		bucket.setStatus(statusValue);

		bucketRepository.save(bucket);
		return BucketResponseDto.BucketInfo.from(bucket);

	}

	public void deleteBucket(Long bucketId) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));

		bucketRepository.delete(bucket);

		BucketResponseDto.BucketInfo.from(bucket);
	}

	public List<BucketResponseDto.BucketInfo> getAllBuckets() {
		List<Bucket> buckets = bucketRepository.findAll();

		return buckets.stream()
			.map(BucketResponseDto.BucketInfo::from)
			.collect(Collectors.toList());
	}

	public BucketResponseDto.BucketInfo getBucketById(Long bucketId) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));

		return BucketResponseDto.BucketInfo.from(bucket);
	}

	public List<RecommendationResponseDto.RecommendedBucketInfo> getRecommendedBuckets(BucketTagType tagType) {

		if (tagType == null) {
			// isRecommended가 true인 버킷만 조회
			List<Bucket> recommendedBuckets = bucketRepository.findByIsRecommendedTrue();

			// 버킷을 태그 타입별로 그룹화
			Map<BucketTagType, List<Bucket>> groupedBuckets = recommendedBuckets
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

					// RecommendationResponseDto.RecommendedBucketInfo의 from 메서드를 사용하여 반환
					return RecommendationResponseDto.RecommendedBucketInfo.builder()
						.tagType(entry.getKey()) // 태그 타입
						.recommendations(topRecommendations) // 추천 리스트
						.build();
				})
				.toList();
		} else {
			// tagType에 맞는 버킷만 조회
			List<Bucket> recommendedBuckets = bucketRepository.findByTagType(tagType);

			// 리턴되는 버킷들을 `RecommendationResponseDto`로 변환하여 반환
			return recommendedBuckets.stream()
				.map(RecommendationResponseDto.RecommendedBucketInfo::from)
				.collect(Collectors.toList());
		}
	}

	public Bucket getBucket(Long bucketId) {
		return bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 Bucket이 존재하지 않습니다."));
	}
}
