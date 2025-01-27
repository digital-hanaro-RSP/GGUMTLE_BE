package com.hana4.ggumtle.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
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

	private boolean checkValidUser(User user, Bucket bucket) {
		return user.getId().equals(bucket.getUser().getId());
	}

	public BucketResponseDto.BucketInfo createBucket(BucketRequestDto.CreateBucket requestDto, User user) {
		// 추천하는 버킷 (isRecommended = true, originId = null)
		if (Boolean.TRUE.equals(requestDto.getIsRecommended())) {
			if (requestDto.getOriginId() != null) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER, "추천하는 버킷은 originId를 가질 수 없습니다.");
			}
			if (requestDto.getFollowers() == null) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER, "추천하는 버킷은 followers를 가져야 합니다.");
			}
		}
		// 추천된 버킷에서 생성되는 경우 (isRecommended = false, originId != null)
		else if (requestDto.getOriginId() != null) {
			// originId에 해당하는 버킷을 조회
			Bucket originBucket = bucketRepository.findById(requestDto.getOriginId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Origin Bucket을 찾을 수 없습니다."));

			// originBucket의 followers 값 증가
			originBucket.setFollowers(originBucket.getFollowers() + 1);
			bucketRepository.save(originBucket); // 변경 사항 저장
		}
		// DreamAccount 조회
		DreamAccount dreamAccount = dreamAccountRepository.findByUserId(user.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "DreamAccount를 찾을 수 없습니다."));

		// 새로운 버킷 생성
		Bucket bucket = bucketRepository.save(requestDto.toEntity(user, dreamAccount));

		return BucketResponseDto.BucketInfo.from(bucket);
	}

	public BucketResponseDto.BucketInfo updateBucket(User user, Long bucketId,
		BucketRequestDto.CreateBucket requestDto) {
		Bucket beforeBucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));

		if (!checkValidUser(user, beforeBucket)) {
			throw new CustomException(ErrorCode.FORBIDDEN, "수정 권한이 없는 사용자입니다.");
		}

		DreamAccount dreamAccount = dreamAccountRepository.findByUserId(user.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "DreamAccount를 찾을 수 없습니다."));

		LocalDateTime parsedDueDate = null;
		if (requestDto.getDueDate() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse(requestDto.getDueDate(), formatter);
			parsedDueDate = localDate.atStartOfDay(); // LocalDate -> LocalDateTime (00:00:00)
		}
		// Update the fields of the bucket using the builder pattern
		Bucket updatedBucket = beforeBucket.toBuilder()
			.user(user)
			.dreamAccount(dreamAccount)
			.safeBox(beforeBucket.getSafeBox())
			.title(requestDto.getTitle())
			.tagType(requestDto.getTagType())
			.dueDate(parsedDueDate)
			.howTo(requestDto.getHowTo())
			.isDueSet(requestDto.getIsDueSet())
			.isAutoAllocate(requestDto.getIsAutoAllocate())
			.allocateAmount(requestDto.getAllocateAmount())
			.cronCycle(requestDto.getCronCycle())
			.goalAmount(requestDto.getGoalAmount())
			.memo(requestDto.getMemo())
			.isRecommended(requestDto.getIsRecommended())
			.originId(requestDto.getOriginId())
			.followers(beforeBucket.getFollowers()) // Assuming followers remain the same or can be modified as needed
			.build();

		// Save the updated bucket
		bucketRepository.save(updatedBucket);

		return BucketResponseDto.BucketInfo.from(updatedBucket);
	}

	public BucketResponseDto.BucketInfo updateBucketStatus(User user, Long bucketId,
		BucketRequestDto.UpdateBucketStatus updates) {
		BucketStatus statusValue = updates.getStatus();

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));
		if (!checkValidUser(user, bucket)) {
			throw new CustomException(ErrorCode.FORBIDDEN, "수정 권한이 없는 사용자입니다.");
		}
		// Enum 변환
		bucket.setStatus(statusValue);

		bucketRepository.save(bucket);
		return BucketResponseDto.BucketInfo.from(bucket);

	}

	public void deleteBucket(Long bucketId, User user) {
		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷을 찾을 수 없습니다."));

		if (!checkValidUser(user, bucket)) {
			throw new CustomException(ErrorCode.FORBIDDEN, "삭제 권한이 없는 사용자입니다.");
		}

		bucketRepository.delete(bucket);

		BucketResponseDto.BucketInfo.from(bucket);
	}

	public List<BucketResponseDto.BucketInfo> getAllBuckets(User user) {
		List<Bucket> buckets = bucketRepository.findAllByUserId(user.getId());

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
			List<Bucket> recommendedBuckets = bucketRepository.findByTagTypeAndIsRecommendedTrue(tagType);

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

	public List<Bucket> getBucketsDueAfter(String userId, LocalDate dueDate) {
		LocalDateTime startOfDay = dueDate.atStartOfDay();

		return bucketRepository.findByUserIdAndDueDateIsNullOrDueDateAfter(userId, startOfDay);
	}
}
