package com.hana4.ggumtle.model.entity.post;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class PostRequestDto {
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	@EqualsAndHashCode
	public static class Write {
		private String imageUrls;

		@NotEmpty(message = "내용을 입력하세요.")
		private String content;

		@NotNull(message = "글 타입을 입력하세요.")
		private PostType postType;

		public Post toEntity(User user, Group group) {
			return Post.builder()
				.user(user).group(group)
				.imageUrls(this.imageUrls)
				.content(this.content)
				.postType(this.postType)
				.build();
		}
	}
}
