package com.hana4.ggumtle.dto.group;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GroupResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class Create extends BaseDto {
		private Long id;
		private String name;
		private GroupCategory category;
		private String description;
		private String imageUrl;

		public static Create from(Group group) {
			return Create.builder()
				.id(group.getId())
				.name(group.getName())
				.category(group.getCategory())
				.description(group.getDescription())
				.imageUrl(group.getImageUrl())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class Read extends BaseDto {
		private Long id;
		private String name;
		private GroupCategory category;
		private String description;
		private String imageUrl;
		private int memberCount;    //count 쿼리 짜서 불러와야함.

		public static Read from(Group group, int memberCount) {
			return Read.builder()
				.id(group.getId())
				.name(group.getName())
				.category(group.getCategory())
				.description(group.getDescription())
				.imageUrl(group.getImageUrl())
				.memberCount(memberCount)
				.build();
		}
	}
}
