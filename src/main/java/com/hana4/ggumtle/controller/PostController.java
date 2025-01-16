package com.hana4.ggumtle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostDetail>> getPost(@PathVariable("postId") Long postId,
		@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(
			CustomApiResponse.success(postService.getPost(customUserDetails.getUser(), groupId, postId)));
	}

	@GetMapping("/post")
	public ResponseEntity<CustomApiResponse<List<PostResponseDto.PostInfo>>> getPostsByPage(
		@PathVariable("groupId") Long groupId, @RequestParam(required = false, defaultValue = "0") int page,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		return ResponseEntity.ok(
			CustomApiResponse.success(postService.getPostsByPage(groupId, customUserDetails.getUser(), page)));
	}

	@PatchMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostInfo>> updatePost(@PathVariable("postId") Long postId,
		@PathVariable Long groupId, @RequestBody @Valid PostRequestDto.Write write,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		return ResponseEntity.ok(
			CustomApiResponse.success(postService.updatePost(customUserDetails.getUser(), groupId, postId, write)));
	}
}

