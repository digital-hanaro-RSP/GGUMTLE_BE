package com.hana4.ggumtle.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class UserRequestDto {
	@Schema(description = "회원가입 DTO")
	@Getter
	@Builder(toBuilder = true) // 기존 필드 값은 유지하고 변경하려는 필드만 수정된 값으로 교체
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Register {
		@Schema(description = "사용자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "이름을 입력하세요.")
		private String name;

		@Schema(description = "전화번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "전화번호를 입력하세요.")
		private String tel;

		@Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "비밀번호를 입력하세요.")
		private String password;

		@Schema(description = "생년월일", example = "1990-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "생년월일을 입력하세요. (예: yyyy-MM-dd)")
		private String birthDate;

		@Schema(description = "성별 (m: 남성, f: 여성)", example = "m", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "성별을 입력하세요.")
		@Pattern(regexp = "[mf]", message = "성별은 'm' 또는 'f'로 입력하세요.")
		private String gender;

		@Schema(description = "프로필 이미지 url", example = "https://example.com/profile.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String profileImageUrl;

		@Schema(description = "닉네임", example = "익명의고라니", requiredMode = Schema.RequiredMode.REQUIRED)
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
				.profileImageUrl(this.profileImageUrl)
				.build();
		}
	}

	@Schema(description = "사용자 로그인 요청 DTO")
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Login {
		@Schema(description = "전화번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "전화번호를 입력하세요.")
		private String tel;

		@Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "비밀번호를 입력하세요.")
		private String password;
	}

	@Schema(description = "토큰 갱신 요청 DTO")
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Refresh {
		@Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "refreshToken을 입력하세요.")
		private String refreshToken;
	}

	@Schema(description = "인증번호 요청 DTO")
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class VerificationCode {
		@Schema(description = "인증번호를 발송할 전화번호", example = "\"01012341234\"", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "전화번호을 입력하세요.")
		private String tel;
	}

	@Schema(description = "인증번호 확인 요청 DTO")
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Validation {
		@Schema(description = "인증번호를 발송할 전화번호", example = "\"01012341234\"", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "전화번호를 입력하세요.")
		private String tel;

		@Schema(description = "사용자가 입력한 인증번호", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "전화번호를 입력하세요.")
		private String code;
	}

	@Schema(description = "사용자 정보 수정 요청 DTO")
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@Generated
	public static class UpdateUser {
		@Schema(description = "새 비밀번호", example = "newpassword123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String password;

		@Schema(description = "새 프로필 이미지 URL", example = "https://example.com/new-profile.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String profileImageUrl;

		@Schema(description = "새 닉네임", example = "새로운닉네임", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String nickname;

		public void validate() {
			if (password == null && profileImageUrl == null && nickname == null) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER, "수정할 정보를 최소 하나 이상 입력해야 합니다.");
			}
		}
	}

}
