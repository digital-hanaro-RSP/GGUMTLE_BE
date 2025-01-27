package com.hana4.ggumtle.dto.comment;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.model.entity.comment.Comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class CommentResponseDto {
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	@SuperBuilder
	public static class CommentInfo extends BaseDto {
		private Long id;
		private Long postId;
		private String content;
		private UserResponseDto.BriefInfo userBriefInfo;
		private boolean isLiked;
		private boolean isMine;
		private int likeCount;

		public static CommentInfo from(Comment comment, boolean isLiked, boolean isMine, int likeCount) {
			return CommentInfo.builder()
				.id(comment.getId())
				.postId(comment.getPost().getId())
				.content(comment.getContent())
				.userBriefInfo(UserResponseDto.BriefInfo.from(comment.getUser()))
				.isLiked(isLiked)
				.isMine(isMine)
				.likeCount(likeCount)
				.createdAt(comment.getCreatedAt())
				.updatedAt(comment.getUpdatedAt())
				.build();
		}
	}
}
