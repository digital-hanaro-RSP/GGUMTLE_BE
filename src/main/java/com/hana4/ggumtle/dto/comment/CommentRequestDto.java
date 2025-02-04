package com.hana4.ggumtle.dto.comment;

import com.hana4.ggumtle.model.entity.comment.Comment;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Generated
@Schema(description = "댓글 요청 DTO")
public class CommentRequestDto {

	@Schema(name = "CommentWriteRequest", description = "댓글 작성 요청 DTO")
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	@EqualsAndHashCode
	public static class CommentWrite {

		@Schema(description = "댓글 내용", example = "이것은 댓글입니다.")
		@NotEmpty(message = "내용을 입력하세요.")
		private String content;

		public Comment toEntity(Post post, User user) {
			return Comment.builder()
				.content(this.content)
				.post(post)
				.user(user)
				.build();
		}
	}
}

