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
		@PathVariable Long groupId, @RequestBody @Valid PostRequestDto.Write write,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(
			CustomApiResponse.success(postService.save(groupId, write, customUserDetails.getUser())));
	}

	@GetMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostDetail>> getPost(@PathVariable Long groupId,
		@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(
			CustomApiResponse.success(postService.getPost(groupId, postId, customUserDetails.getUser())));
	}

	@GetMapping("/post")
	public ResponseEntity<CustomApiResponse<List<PostResponseDto.PostInfo>>> getPostsByPage(
		@PathVariable Long groupId, @RequestParam(required = false, defaultValue = "0") int page,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		return ResponseEntity.ok(
			CustomApiResponse.success(postService.getPostsByPage(groupId, page, customUserDetails.getUser())));
	}

	@PatchMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostInfo>> updatePost(@PathVariable Long groupId,
		@PathVariable Long postId, @RequestBody @Valid PostRequestDto.Write write,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		return ResponseEntity.ok(
			CustomApiResponse.success(postService.updatePost(groupId, postId, write, customUserDetails.getUser())));
	}
}

