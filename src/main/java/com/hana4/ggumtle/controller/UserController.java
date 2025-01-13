package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	public final UserService userService;

	@PostMapping
	public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> register(@RequestBody @Valid UserRequestDto.Register request) {
		return ResponseEntity.ok(ApiResponse.success(userService.register(request)));
	}
}
