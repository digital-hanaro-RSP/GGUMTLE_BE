package com.hana4.ggumtle.dto.groupMember;

import java.time.LocalDateTime;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Generated
public class GroupMemberResponseDto {
	private CustomApiResponse<JoinGroup> response;

	@Getter
	@SuperBuilder
	public static class JoinGroup extends BaseDto {
		@Schema(description = "그룹 내 멤버ID", example = "1")
		private Long id;

		@Schema(description = "가입 할 그룹 ID", example = "group123")
		private Group groupId;

		@Schema(description = "가입자 UserID", example = "user123")
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
