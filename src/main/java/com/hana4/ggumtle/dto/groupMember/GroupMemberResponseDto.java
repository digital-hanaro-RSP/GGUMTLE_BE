package com.hana4.ggumtle.dto.groupMember;

import java.time.LocalDateTime;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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

	@Getter
	@Builder
	@AllArgsConstructor
	public static class LeaveGroup {
		private Long id;
		private Group groupId;
		private User userId;
		private LocalDateTime leavedAt;

		public static LeaveGroup from(GroupMember groupMember) {
			return LeaveGroup.builder()
				.id(groupMember.getId())
				.groupId(groupMember.getGroup())
				.userId(groupMember.getUser())
				.leavedAt(LocalDateTime.now())
				.build();
		}
	}
}
