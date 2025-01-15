package com.hana4.ggumtle.dto.groupMember;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;

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

		public GroupMember toEntity() {
			return new GroupMember().toBuilder()
				.group(this.groupId)
				.build();
		}
	}
}
