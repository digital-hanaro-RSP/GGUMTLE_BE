package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입, 로그인, 토큰 재발급, 약관동의 API")
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
	@PostMapping("/user")
	public ResponseEntity<CustomApiResponse<UserResponseDto.UserInfo>> register(
		@RequestBody @Valid UserRequestDto.Register request) {
		return ResponseEntity.ok(CustomApiResponse.success(userService.register(request)));
	}

	@Operation(summary = "사용자 로그인", description = "사용자 로그인을 처리합니다.")
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
	@PatchMapping("/mydata/permission")
	public ResponseEntity<CustomApiResponse<UserResponseDto.UserInfo>> updatePermission(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(CustomApiResponse.success(userService.updatePermission(userDetails.getUser())));
	}
}
