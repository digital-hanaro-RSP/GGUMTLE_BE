package com.hana4.ggumtle.dto.post;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.post.PostType;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Generated
@Schema(description = "게시물 요청 DTO")
public class PostRequestDto {

	@Schema(name = "PostWriteRequest", description = "게시물 작성 요청 DTO")
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	@EqualsAndHashCode
	public static class Write {

		@Schema(description = "이미지 URL들", example = "http://example.com/image1.jpg,http://example.com/image2.jpg")
		private String imageUrls;

		@Schema(description = "게시물 내용", example = "오늘은 좋은 날씨입니다.")
		@NotEmpty(message = "내용을 입력하세요.")
		private String content;

		@Schema(description = "스냅샷", example = "2023년 6월 15일의 스냅샷")
		@NotEmpty(message = "스냅샷이 필요합니다.")
		private String snapShot;

		@Schema(description = "게시물 타입", example = "POST", defaultValue = "POST")
		@NotNull(message = "글 타입을 입력하세요.")
		@Builder.Default
		private PostType postType = PostType.POST;

		public Post toEntity(User user, Group group) {
			return Post.builder()
				.user(user)
				.group(group)
				.imageUrls(this.imageUrls)
				.content(this.content)
				.snapshot(this.snapShot)
				.postType(this.postType)
				.build();
		}
	}

	@Schema(name = "PostShareRequest", description = "게시물 공유 요청 DTO")
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Builder
	@EqualsAndHashCode
	public static class Share {

		@Schema(description = "공유 내용", example = "이 뉴스를 공유합니다.")
		@NotEmpty(message = "내용을 입력하세요.")
		private String content;

		@Schema(description = "게시물 타입", example = "NEWS", defaultValue = "NEWS")
		@NotNull(message = "글 타입을 입력하세요.")
		@Builder.Default
		private PostType postType = PostType.NEWS;

		public Post toEntity(User user, Group group) {
			return Post.builder()
				.user(user)
				.group(group)
				.content(this.content)
				.postType(this.postType)
				.build();
		}
	}
}

