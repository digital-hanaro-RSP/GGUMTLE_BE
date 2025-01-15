package com.hana4.ggumtle.dto.post;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.post.PostType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class PostResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class PostInfo extends BaseDto {
		private Long id;
		private String userId;
		private Long groupId;
		private Long bucketId;
		private String snapShot;
		private String imageUrls;
		private String content;
		private PostType postType;

		public static PostInfo from(Post post) {
			return PostInfo.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.groupId(post.getGroup().getId())
				.bucketId(post.getBucketId())
				.snapShot(post.getSnapshot())
				.imageUrls(post.getImageUrls())
				.content(post.getContent())
				.postType(post.getPostType())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build();
		}
	}
}
