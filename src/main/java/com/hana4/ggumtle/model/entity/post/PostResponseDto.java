package com.hana4.ggumtle.model.entity.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Generated
public class PostResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@Builder
	public static class PostInfo {
		private String userId;
		private Long groupId;
		private Long bucketId;
		private String snapShot;
		private String imageUrls;
		private String content;
		private PostType postType;

		public static PostInfo from(Post post) {
			return PostInfo.builder()
				.userId(post.getUser().getId())
				.groupId(post.getGroup().getId())
				.bucketId(post.getBucketId())
				.snapShot(post.getSnapshot())
				.imageUrls(post.getImageUrls())
				.content(post.getContent())
				.postType(post.getPostType())
				.build();
		}
	}
}
