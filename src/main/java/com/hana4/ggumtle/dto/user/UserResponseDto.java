package com.hana4.ggumtle.dto.user;

import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class UserResponseDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class UserInfo {
		private String id;
		private String tel;
		private String name;
		private short permission;
		private LocalDateTime birthDate;
		private String gender;
		private UserRole role;
		private String profileImageUrl;
		private String nickname;

		public static UserInfo from(User user) {
			return UserInfo.builder()
				.id(user.getId())
				.tel(user.getTel())
				.name(user.getName())
				.permission(user.getPermission())
				.birthDate(user.getBirthDate())
				.gender(user.getGender())
				.role(user.getRole())
				.profileImageUrl(user.getProfileImageUrl())
				.nickname(user.getNickname())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class Login {
		private String accessToken;
		private String refreshToken;
		private short permission;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class Refresh {
		private String accessToken;
		private String refreshToken;
	}
}
