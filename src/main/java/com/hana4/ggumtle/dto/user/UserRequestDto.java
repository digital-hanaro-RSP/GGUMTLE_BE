package com.hana4.ggumtle.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hana4.ggumtle.model.entity.user.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDto {
	@Getter
	@Builder(toBuilder = true) // 기존 필드 값은 유지하고 변경하려는 필드만 수정된 값으로 교체
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Register {
		@NotEmpty(message = "이름을 입력하세요.")
		private String name;

		@NotEmpty(message = "전화번호를 입력하세요.")
		private String tel;

		@NotEmpty(message = "비밀번호를 입력하세요.")
		private String password;

		@NotEmpty(message = "생년월일을 입력하세요. (예: yyyy-MM-dd)")
		private String birthDate;

		@NotNull(message = "성별을 입력하세요.")
		@Pattern(regexp = "[mf]", message = "성별은 'm' 또는 'f'로 입력하세요.")
		private String gender;

		@NotEmpty(message = "닉네임을 입력하세요.")
		private String nickname;

		public User toEntity() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse(this.birthDate, formatter);

			// LocalDate -> LocalDateTime (default 00:00:00)
			LocalDateTime parsedBirthDate = localDate.atStartOfDay();

			return new User().toBuilder()
				.name(this.name)
				.tel(this.tel)
				.password(this.password)
				.birthDate(parsedBirthDate)
				.gender(this.gender)
				.nickname(this.nickname)
				.build();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Login {
		@NotEmpty(message = "전화번호를 입력하세요.")
		private String tel;

		@NotEmpty(message = "비밀번호를 입력하세요.")
		private String password;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Refresh {
		@NotEmpty(message = "refreshToken을 입력하세요.")
		private String refreshToken;
	}
}
