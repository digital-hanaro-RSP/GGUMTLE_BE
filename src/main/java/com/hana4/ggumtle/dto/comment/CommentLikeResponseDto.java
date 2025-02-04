package com.hana4.ggumtle.dto.comment;

import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.commentLike.CommentLike;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Generated
@Schema(description = "댓글 좋아요 응답 DTO")
public class CommentLikeResponseDto {

	@Schema(name = "CommentLikeInfoResponse", description = "댓글 좋아요 정보 응답 DTO")
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@SuperBuilder
	public static class CommentLikeInfo {
		@Schema(description = "댓글 좋아요 ID", example = "1")
		private Long id;

		@Schema(description = "사용자 ID", example = "user123")
		private String userId;

		@Schema(description = "댓글 ID", example = "100")
		private Long commentId;

		@Schema(description = "생성 일시", example = "2023-06-15T14:30:00")
		private LocalDateTime createdAt;

		public static CommentLikeInfo from(CommentLike commentLike) {
			return CommentLikeInfo.builder()
				.id(commentLike.getId())
				.userId(commentLike.getUser().getId())
				.commentId(commentLike.getComment().getId())
				.createdAt(commentLike.getCreatedAt())
				.build();
		}
	}
}
