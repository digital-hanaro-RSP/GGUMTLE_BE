package com.hana4.ggumtle.dto.comment;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.model.entity.comment.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
@Schema(description = "댓글 응답 DTO")
public class CommentResponseDto {

	@Schema(name = "CommentInfoResponse", description = "댓글 정보 응답 DTO")
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	@SuperBuilder
	public static class CommentInfo extends BaseDto {
		@Schema(description = "댓글 ID", example = "1")
		private Long id;

		@Schema(description = "게시물 ID", example = "100")
		private Long postId;

		@Schema(description = "댓글 내용", example = "이것은 댓글입니다.")
		private String content;

		@Schema(description = "사용자 간단 정보")
		private UserResponseDto.BriefInfo userBriefInfo;

		@Schema(description = "좋아요 여부", example = "true")
		private boolean isLiked;

		@Schema(description = "본인 댓글 여부", example = "false")
		private boolean isMine;

		@Schema(description = "좋아요 수", example = "5")
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
