package com.hana4.ggumtle.dto.groupMember;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;

import lombok.experimental.SuperBuilder;

public class GroupMemberResponseDto {
	private ApiResponse<JoinGroup> response;

	@SuperBuilder
	public static class JoinGroup extends BaseDto {
		private Long id;
		private Group groupId;
		private User userId;

		public static JoinGroup from(GroupMember groupMember) {
			return JoinGroup.builder()
				.id(groupMember.getId())
				.groupId(groupMember.getGroup())
				.userId(groupMember.getUser())
				.createdAt(groupMember.getCreatedAt())
				.updatedAt(groupMember.getUpdatedAt())
				.build();
		}
	}
}
