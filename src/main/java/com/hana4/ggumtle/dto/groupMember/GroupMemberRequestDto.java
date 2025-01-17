package com.hana4.ggumtle.dto.groupMember;

import com.hana4.ggumtle.model.entity.groupMember.GroupMember;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class GroupMemberRequestDto {
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Create {
		@Schema(description = "가입 할 그룹 ID", example = "group123")
		@NotNull(message = "그룹 ID를 입력하세요.")
		private Long groupId;

		public GroupMember toEntity() {
			return new GroupMember().toBuilder()
				// .group(this.groupId)
				.build();
		}
	}
}
