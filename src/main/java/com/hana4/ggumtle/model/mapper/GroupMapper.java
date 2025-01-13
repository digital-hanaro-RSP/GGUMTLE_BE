package com.hana4.ggumtle.model.mapper;

import com.hana4.ggumtle.model.dto.GroupResponseDto;
import com.hana4.ggumtle.model.entity.group.Group;

public class GroupMapper {
		public static GroupResponseDto toDto(Group group) {
				if (group == null) {
						return null;
				}
				return GroupResponseDto.builder()
						.id(group.getId())
						.name(group.getName())
						.category(String.valueOf(group.getCategory()))
						.description(group.getDescription())
						.imageUrl(group.getImageUrl())
						.build();
		}
}
