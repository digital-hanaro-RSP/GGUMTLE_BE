package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
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

	@PostMapping("/user")
	public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> register(@RequestBody @Valid UserRequestDto.Register request) {
		return ResponseEntity.ok(ApiResponse.success(userService.register(request)));
	}

	@PostMapping("/tokens")
	public ResponseEntity<ApiResponse<UserResponseDto.Login>> login(@RequestBody @Valid UserRequestDto.Login request, HttpServletResponse response) {
		// 로그인 후 accessToken과 refreshToken 생성
		UserResponseDto.Login loginResponse = userService.login(request);

		setRefreshTokenCookie(response, loginResponse.getRefreshToken());
		return ResponseEntity.ok(ApiResponse.success(loginResponse));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<UserResponseDto.Refresh>> tokenRefresh(@RequestBody @Valid UserRequestDto.Refresh refreshTokenRequestDTO, HttpServletResponse response) {
		// token 재발급
		UserResponseDto.Refresh refreshResponse = userService.refresh(refreshTokenRequestDTO);

		setRefreshTokenCookie(response, refreshResponse.getRefreshToken());
		return ResponseEntity.ok(ApiResponse.success(refreshResponse));
	}
}
