package com.hana4.ggumtle.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated
public class GroupResponseDto {
	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@SuperBuilder
	public static class Create extends BaseDto {
		@Schema(description = "그룹 ID", example = "1")
		private Long id;

		@Schema(description = "그룹 이름", example = "재태크모임")
		private String name;

		@Schema(description = "그룹 카테고리", example = "INVESTMENT")
		private GroupCategory category;

		@Schema(description = "그룹 설명", example = "재태크 왕이 될거야!")
		private String description;

		@Schema(description = "그룹 이미지Url", example = "http://example.com/image.jpg")
		private String imageUrl;

		@Schema(description = "그룹 멤버 수", example = "2")
		private int memberCount;

		public static Create from(Group group, int memberCount) {
			return Create.builder()
				.id(group.getId())
				.name(group.getName())
				.category(group.getCategory())
				.description(group.getDescription())
				.imageUrl(group.getImageUrl())
				.memberCount(memberCount)
				.createdAt(group.getCreatedAt())
				.updatedAt(group.getUpdatedAt())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@SuperBuilder
	public static class Read extends BaseDto {
		private Long id;
		private String name;
		private GroupCategory category;
		private String description;
		private String imageUrl;
		private int memberCount;

		public static Read from(Group group, int memberCount) {
			return Read.builder()
				.id(group.getId())
				.name(group.getName())
				.category(group.getCategory())
				.description(group.getDescription())
				.imageUrl(group.getImageUrl())
				.memberCount(memberCount)
				.createdAt(group.getCreatedAt())
				.updatedAt(group.getUpdatedAt())
				.build();
		}
	}
}
