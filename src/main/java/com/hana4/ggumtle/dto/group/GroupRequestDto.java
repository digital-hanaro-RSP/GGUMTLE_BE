package com.hana4.ggumtle.dto.group;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GroupRequestDto {

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Create {
		@NotEmpty(message = "그룹이름을 입력하세요.")
		private String name;
		@NotNull(message = "그룹 카테고리를 입력하세요.")
		private GroupCategory category;
		@NotEmpty(message = "설명을 입력하세요.")
		private String description;
		@NotEmpty(message = "이미지 url을 입력하세요.")
		private String imageUrl;

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
