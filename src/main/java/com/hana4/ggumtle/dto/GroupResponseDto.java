package com.hana4.ggumtle.dto;

import com.hana4.ggumtle.model.entity.group.Group;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GroupResponseDto extends BaseDto {
	private Long id;
	private String name;
	private String category;
	private String description;
	private String imageUrl;
	private int memberCount;

	public GroupResponseDto(Group group) {

	}

	public static GroupResponseDto from(Group group) {
		return new GroupResponseDto(group);
	}
}
