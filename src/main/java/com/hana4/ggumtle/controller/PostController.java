package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/group/{groupId}")
public class PostController {
	private final PostService postService;

	@PostMapping("/post")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostInfo>> writePost(
		@RequestBody @Valid PostRequestDto.Write write, @PathVariable("groupId") Long groupId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(
			CustomApiResponse.success(postService.save(groupId, customUserDetails.getUser(), write)));
	}
}

