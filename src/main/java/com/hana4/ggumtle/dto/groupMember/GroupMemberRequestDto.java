package com.hana4.ggumtle.dto.groupMember;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GroupMemberRequestDto {
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Create {
		@NotEmpty(message = "그룹 이름을 입력하세요.")
		private Group groupId;
		@NotEmpty(message = "가입 유저 이름을 입력하세요.")
		private User userId;

		public GroupMember toEntity() {
			return new GroupMember().toBuilder()
				.group(this.groupId)
				.user(this.userId)
				.build();
		}
	}
}
