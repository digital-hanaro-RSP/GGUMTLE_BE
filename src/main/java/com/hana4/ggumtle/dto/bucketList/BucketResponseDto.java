package com.hana4.ggumtle.dto.bucketList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "버킷리스트 응답 DTO")
@Generated
public class BucketResponseDto {
	@Schema(description = "버킷리스트 정보 응답")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class BucketInfo {
		@Schema(description = "사용자 ID", example = "user123")
		private Long id;

		@Schema(description = "버킷리스트 제목", example = "유럽 여행", requiredMode = Schema.RequiredMode.REQUIRED)
		private String title;

		@Schema(description = "버킷리스트 태그타입", example = "GO", requiredMode = Schema.RequiredMode.REQUIRED)
		private BucketTagType tagType;

		@Schema(description = "버킷리스트 완료날짜", example = "1990-01-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
		private LocalDateTime dueDate;

		@Schema(description = "버킷리스트 종류", example = "MONEY", requiredMode = Schema.RequiredMode.REQUIRED)
		private BucketHowTo howTo;

		@Schema(description = "버킷리스트 완료날짜가 설정이 됐는지", example = "true")
		private Boolean isDueSet;

		@Schema(description = "자동분배 설정 유무", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
		private Boolean isAutoAllocate;

		@Schema(description = "자동분배 할당 액수", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
		private BigDecimal allocateAmount;

		@Schema(description = "자동분배 주기", example = "0 0 1 * *", requiredMode = Schema.RequiredMode.REQUIRED)
		private String cronCycle;

		@Schema(description = "버킷리스트 목표 금액", example = "10000000", requiredMode = Schema.RequiredMode.REQUIRED)
		private BigDecimal goalAmount;

		@Schema(description = "버킷리스트 메모", example = "여행 꼭 가고싶다", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String memo;

		@Schema(description = "버킷리스트 상태", example = "DOING", requiredMode = Schema.RequiredMode.REQUIRED)
		private BucketStatus status;

		@Schema(description = "추천 버킷리스트인지 여부", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private Boolean isRecommended;

		@Schema(description = "추천 버킷이라면 오리지널 아이디", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
		private Long originId;

		@Schema(description = "safeBox 액수", example = "1000.00", requiredMode = Schema.RequiredMode.REQUIRED)
		private BigDecimal safeBox;

		@Schema(description = "팔로워 수", example = "3000", requiredMode = Schema.RequiredMode.REQUIRED)
		private Long followers;

		@Schema(description = "사용자 ID", example = "user123")
		private String userId;

		@Schema(description = "꿈통장 ID", example = "1")
		private Long dreamAccountId;

		// @Schema(description = "꿈통장 ID", example = "1")
		// private List<Recommendation> recommendations;

		public static BucketInfo from(Bucket bucket) {

			return BucketInfo.builder()
				.dreamAccountId(bucket.getDreamAccount().getId())
				.userId(bucket.getUser().getId())
				.id(bucket.getId())
				.title(bucket.getTitle())
				.tagType(bucket.getTagType())
				.dueDate(bucket.getDueDate())
				.howTo(bucket.getHowTo())
				.isDueSet(bucket.getIsDueSet())
				.isAutoAllocate(bucket.getIsAutoAllocate())
				.allocateAmount(bucket.getAllocateAmount())
				.cronCycle(bucket.getCronCycle())
				.goalAmount(bucket.getGoalAmount())
				.memo(bucket.getMemo())
				.status(bucket.getStatus())
				.safeBox(bucket.getSafeBox())
				.isRecommended(bucket.getIsRecommended())
				.originId(bucket.getOriginId())
				.followers(bucket.getFollowers())
				.build();
		}
	}
}
