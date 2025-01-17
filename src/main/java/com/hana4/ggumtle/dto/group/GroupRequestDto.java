package com.hana4.ggumtle.dto.group;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Generated
public class GroupRequestDto {

	@Schema(description = "꿈모임 그룹 DTO")
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Create {
		@Schema(description = "그룹 이름", example = "재태크모임", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "그룹이름을 입력하세요.")
		private String name;

		@Schema(description = "그룹 카테고리", example = "INVESTMENT", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "그룹 카테고리를 입력하세요.")
		private GroupCategory category;

		@Schema(description = "그룹 설명", example = "투자왕들의 모임", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "설명을 입력하세요.")
		private String description;

		@Schema(description = "그룹 프로필 이미지", example = "http://example.com/image.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "이미지 url을 입력하세요.")
		private String imageUrl;

		public static Create from(Group group) {
			return Create.builder()
				.name(group.getName())
				.category(group.getCategory())
				.description(group.getDescription())
				.imageUrl(group.getImageUrl())
				.build();
		}

		public Group toEntity() {
			return new Group().toBuilder()
				.name(this.name)
				.category(this.category)
				.description(this.description)
				.imageUrl(this.imageUrl)
				.build();
		}
	}
}
