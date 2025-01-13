package com.hana4.ggumtle.model.mapper;

import com.hana4.ggumtle.model.dto.JoinGroupSuccessResponseDto;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;

public class GroupMemberMapper {
		public static JoinGroupSuccessResponseDto.JoinGroupDataDto toDto(GroupMember groupMember) {
				if (groupMember == null) {
						return null;
				}
				return JoinGroupSuccessResponseDto.JoinGroupDataDto.builder()
						.id(String.valueOf(groupMember.getId()))
						.groupId(groupMember.getGroup().getId())
						.userId(Long.valueOf(groupMember.getUser().getId()))
						.build();
		}
}
