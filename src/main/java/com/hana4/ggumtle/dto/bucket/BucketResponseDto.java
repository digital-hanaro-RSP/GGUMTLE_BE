package com.hana4.ggumtle.dto.bucket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class BucketResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class BriefInfo {
		private String title;
		private BucketTagType bucketTagType;
		private LocalDateTime dueDate;
		private BucketHowTo bucketHowTo;
		private BigDecimal goalAmount;
		private BigDecimal safeBox;

		public static BriefInfo from(Bucket bucket) {
			return BriefInfo.builder()
				.title(bucket.getTitle())
				.bucketTagType(bucket.getTagType())
				.dueDate(bucket.getDueDate())
				.bucketHowTo(bucket.getHowTo())
				.goalAmount(bucket.getGoalAmount())
				.safeBox(bucket.getSafeBox())
				.build();
		}
	}
}
