package com.hana4.ggumtle.dto.bucketList;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BucketResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class BucketInfo {
		private Long id;
		private String title;
		private BucketTagType tagType;
		private LocalDateTime dueDate;
		private BucketHowTo howTo;
		private Boolean isDueSet;
		private Boolean isAutoAllocate;
		private BigDecimal allocateAmount;
		private String cronCycle;
		private BigDecimal goalAmount;
		private String memo;
		private BucketStatus status;
		private Boolean isRecommended;
		private Long originId;
		private BigDecimal safeBox;
		private Long followers;
		private String userId;
		private Long dreamAccountId;

		private List<Recommendation> recommendations;

		public static BucketInfo form(Bucket bucket) {
			
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

	@Data
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class Recommendation {
		private Long followers;
		private String title;
	}
}
