package com.hana4.ggumtle.dto.recommendation;

import java.util.List;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class RecommendationResponseDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class RecommendedBucketInfo {
		private BucketTagType tagType;
		private List<Recommendation> recommendations;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Recommendation {
			private Long id;
			private String title;
			private Long followers;
		}

		public static RecommendedBucketInfo from(Bucket bucket) {
			return RecommendedBucketInfo.builder()
				.tagType(bucket.getTagType())
				.recommendations(List.of(
					Recommendation.builder()
						.id(bucket.getId())
						.title(bucket.getTitle())
						.followers(bucket.getFollowers())
						.build()
				))
				.build();
		}
	}
}
