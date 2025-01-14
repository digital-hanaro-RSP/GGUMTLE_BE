package com.hana4.ggumtle.model.entity.post;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/group/{groupId}")
public class PostController {
	private final PostService postService;

	@PostMapping("/post/{userId}")
	public ResponseEntity<ApiResponse<PostResponseDto.PostInfo>> writePost(
		@RequestBody @Valid PostRequestDto.Write write, @PathVariable("groupId") Long groupId,
		@PathVariable("userId") String userId) {
		return ResponseEntity.ok(ApiResponse.success(postService.save(userId, groupId, write)));
	}
}

