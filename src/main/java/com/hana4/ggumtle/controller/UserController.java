package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입, 로그인, 토큰 재발급, 약관동의 API")
@Slf4j
public class UserController {
	public final UserService userService;

	private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);  // JavaScript로 접근하지 못하게 설정
		refreshTokenCookie.setSecure(true);    // HTTPS 환경에서만 사용되도록 설정
		refreshTokenCookie.setPath("/");       // 모든 경로에 대해 쿠키 사용
		refreshTokenCookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 기간 설정 (1일)

		response.addCookie(refreshTokenCookie);
	}

	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답"),
		@ApiResponse(responseCode = "400", description = "중복된 번호로 회원가입을 하려할 때",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 409, \"error\": \"Conflict\", \"message\": \"해당 전화번호를 사용하는 유저가 이미 존재합니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PostMapping("/user")
	public ResponseEntity<CustomApiResponse<UserResponseDto.UserInfo>> register(
		@RequestBody @Valid UserRequestDto.Register request) {
		return ResponseEntity.ok(CustomApiResponse.success(userService.register(request)));
	}

	@Operation(summary = "사용자 로그인", description = "사용자 로그인을 처리합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 전화번호를 사용하는 유저를 찾을 수 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "401", description = "비밀번호 불일치",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"아이디 혹은 비밀번호가 일치하지 않습니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PostMapping("/auth/tokens")
	public ResponseEntity<CustomApiResponse<UserResponseDto.TokensWithPermission>> login(
		@RequestBody @Valid UserRequestDto.Login request,
		HttpServletResponse response) {
		// 로그인 후 accessToken과 refreshToken 생성
		UserResponseDto.TokensWithPermission loginResponse = userService.login(request);

		setRefreshTokenCookie(response, loginResponse.getRefreshToken());
		return ResponseEntity.ok(CustomApiResponse.success(loginResponse));
	}

	@Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"Refresh Token이 만료되었거나 정상적인 Token이 아닙니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PostMapping("/auth/refresh")
	public ResponseEntity<CustomApiResponse<UserResponseDto.Tokens>> tokenRefresh(
		@RequestBody @Valid UserRequestDto.Refresh refreshTokenRequestDto, HttpServletResponse response) {
		// token 재발급
		UserResponseDto.Tokens refreshResponse = userService.refresh(refreshTokenRequestDto);

		setRefreshTokenCookie(response, refreshResponse.getRefreshToken());
		return ResponseEntity.ok(CustomApiResponse.success(refreshResponse));
	}

	@Operation(
		summary = "마이데이터 약관동의",
		description = "user permission을 1로 업데이트합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "권한 업데이트 성공"),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자입니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PatchMapping("/mydata/permission")
	public ResponseEntity<CustomApiResponse<UserResponseDto.UserInfo>> updatePermission(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(CustomApiResponse.success(userService.addMyDataPermission(userDetails.getUser())));
	}

	@Operation(summary = "인증번호 코드 요청", description = "휴대폰 인증번호를 문자로 발송합니다. 같은 번호로 하루 10번 이상 불가.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "인증 문자 발송 성공"),
		@ApiResponse(responseCode = "400", description = "인증 코드가 이미 발송됨. 3분 뒤에 다시 시도 가능.",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 400, \"error\": \"Bad Request\", \"message\": \"인증 코드가 이미 발송되었습니다. 잠시 후 다시 시도해주세요.\" }"
			))),
		@ApiResponse(responseCode = "429", description = "일일 한도 10회 초과",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 429, \"error\": \"Too Many Requests\", \"message\": \"일일 SMS 인증 요청 한도를 초과했습니다. 내일 다시 시도해주세요.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PostMapping("/verification-code")
	public ResponseEntity<CustomApiResponse<Void>> sendVerificationCode(
		@RequestBody @Valid UserRequestDto.VerificationCode request) {
		userService.sendVerificationCode(request.getTel());
		return ResponseEntity.ok(CustomApiResponse.success());
	}

	@Operation(summary = "인증번호 검증", description = "발송된 인증번호의 유효성을 검증합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "인증번호 검증 성공"),
		@ApiResponse(responseCode = "404", description = "인증번호를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"인증 코드를 찾을 수 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "400", description = "인증번호 불일치",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 400, \"error\": \"Bad Request\", \"message\": \"인증 코드가 일치하지 않습니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"인증 코드 검증 중 오류가 발생했습니다.\" }"
			)))
	})
	@PostMapping("/verification-code/validation")
	public ResponseEntity<CustomApiResponse<Void>> validateVerificationCode(
		@RequestBody @Valid UserRequestDto.Validation request
	) {
		userService.validateVerificationCode(request);
		return ResponseEntity.ok(CustomApiResponse.success());
	}

	@Operation(summary = "마이페이지", description = "사용자 정보를 반환합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답"),
		@ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 유저를 찾을 수 없습니다\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@GetMapping("/user")
	public ResponseEntity<CustomApiResponse<UserResponseDto.UserInfo>> getUserInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(CustomApiResponse.success(userService.getUserInfo(userDetails.getUser().getId())));
	}

	@Operation(summary = "마이페이지", description = "사용자 이미지, 닉네임, 비밀번호 수정")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답"),
		@ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 유저를 찾을 수 없습니다\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@PatchMapping("/user")
	public ResponseEntity<CustomApiResponse<UserResponseDto.UserInfo>> updateUserInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid UserRequestDto.UpdateUser updateUserRequest) {

		updateUserRequest.validate();

		return ResponseEntity.ok(
			CustomApiResponse.success(userService.updateUserInfo(userDetails.getUser().getId(), updateUserRequest))
		);
	}

	@Operation(summary = "마이페이지", description = "회원 탈퇴")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 응답"),
		@ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 유저를 찾을 수 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal Server Error\", \"message\": \"내부 서버 오류\" }"
			)))
	})
	@DeleteMapping("/user")
	public ResponseEntity<CustomApiResponse<Void>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		userService.deleteUser(userDetails.getUser().getId());

		return ResponseEntity.ok(CustomApiResponse.success());
	}
}
