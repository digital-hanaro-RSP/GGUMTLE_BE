package com.hana4.ggumtle.dto.post;

import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.postLike.PostLike;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class PostLikeResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class Add {
		private Long id;
		private String userId;
		private Long postId;
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
