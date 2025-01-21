package com.hana4.ggumtle.dto.post;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
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
		private String snapShot;
		private String imageUrls;
		private String content;
		private PostType postType;
		private UserResponseDto.BriefInfo userBriefInfo;
		private GroupCategory groupCategory;
		private boolean isLiked;
		private boolean isMine;

		public static PostInfo from(Post post, boolean isLiked, boolean isMine) {
			return PostInfo.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.groupId(post.getGroup().getId())
				.snapShot(post.getSnapshot())
				.imageUrls(post.getImageUrls())
				.content(post.getContent())
				.postType(post.getPostType())
				.userBriefInfo(UserResponseDto.BriefInfo.from(post.getUser()))
				.groupCategory(post.getGroup().getCategory())
				.isLiked(isLiked)
				.isMine(isMine)
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
		private int likeCount;
		private int commentCount;

		public static PostDetail from(Post post, boolean isLiked, int likeCount, int commentCount, boolean isMine) {
			return PostDetail.builder()
				.id(post.getId())
				.userId(post.getUser().getId())
				.groupId(post.getGroup().getId())
				.snapShot(post.getSnapshot())
				.imageUrls(post.getImageUrls())
				.content(post.getContent())
				.postType(post.getPostType())
				.userBriefInfo(UserResponseDto.BriefInfo.from(post.getUser()))
				.likeCount(likeCount)
				.commentCount(commentCount)
				.isLiked(isLiked)
				.isMine(isMine)
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build();
		}
	}
}
