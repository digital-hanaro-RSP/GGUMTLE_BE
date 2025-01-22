package com.hana4.ggumtle.dto.comment;

import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.commentLike.CommentLike;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Generated
public class CommentLikeResponseDto {
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@SuperBuilder
	public static class CommentLikeInfo {
		private Long id;
		private String userId;
		private Long commentId;
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
