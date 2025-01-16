package com.hana4.ggumtle.dto.post;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
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
		private boolean isLiked;

		public static PostInfo from(Post post, boolean isLiked) {
			return PostInfo.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.groupId(post.getGroup().getId())
				.bucketId(post.getBucketId())
				.snapShot(post.getSnapshot())
				.imageUrls(post.getImageUrls())
				.content(post.getContent())
				.postType(post.getPostType())
				.isLiked(isLiked)
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class PostDetail extends PostInfo {
		private UserResponseDto.BriefInfo userBriefInfo;
		private int likeCount;
		private int commentCount;
		private boolean isLiked;

		public static PostDetail from(Post post, boolean isLiked, int likeCount, int commentCount) {
			return PostDetail.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.groupId(post.getGroup().getId())
				.bucketId(post.getBucketId())
				.snapShot(post.getSnapshot())
				.imageUrls(post.getImageUrls())
				.content(post.getContent())
				.postType(post.getPostType())
				.userBriefInfo(UserResponseDto.BriefInfo.from(post.getUser()))
				.likeCount(likeCount)
				.commentCount(commentCount)
				.isLiked(isLiked)
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build();
		}
	}
}
