package com.hana4.ggumtle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinGroupSuccessResponseDto {
	private int code = 201;
	private String error = null;
	private String message = "ok";
	private JoinGroupDataDto data;

	@SuperBuilder
	public static class JoinGroupDataDto extends BaseDto {
		private String id;
		private Long groupId;
		private Long userId;
	}
}