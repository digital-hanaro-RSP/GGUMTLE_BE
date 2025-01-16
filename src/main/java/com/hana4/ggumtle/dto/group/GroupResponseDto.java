package com.hana4.ggumtle.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponseDto {
	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@SuperBuilder
	public static class Create extends BaseDto {
		private Long id;
		private String name;
		private GroupCategory category;
		private String description;
		private String imageUrl;
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
