package com.hana4.ggumtle.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinGroupRequestDto {
	private Long groupId;
	private Long userId;

	public JoinGroupRequestDto(Long groupId, Long userId) {
		this.groupId = groupId;
		this.userId = userId;
	}
}
