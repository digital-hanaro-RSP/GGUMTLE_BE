package com.hana4.ggumtle.dto.post;

import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.postLike.PostLike;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class PostLikeResponseDto {

	@Schema(name = "PostLikeAddResponse", description = "게시물 좋아요 추가 응답 DTO")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class Add {

		@Schema(
			description = "좋아요 고유 식별자",
			example = "123"
		)
		private Long id;

		@Schema(
			description = "사용자 식별자",
			example = "user-001",
			pattern = "^[a-zA-Z0-9_-]+$"
		)
		private String userId;

		@Schema(
			description = "게시물 식별자",
			example = "456"
		)
		private Long postId;

		@Schema(
			description = "생성 일시 (ISO 8601 형식)",
			example = "2025-02-04T09:30:45"
		)
		private LocalDateTime createdAt;

		public static Add from(PostLike postLike) {
			return Add.builder()
				.id(postLike.getId())
				.userId(postLike.getUser().getId())
				.postId(postLike.getPost().getId())
				.createdAt(postLike.getCreatedAt())
				.build();
		}
	}
}
