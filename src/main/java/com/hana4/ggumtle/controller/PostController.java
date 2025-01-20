package com.hana4.ggumtle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/group/{groupId}")
@Tag(name = "Post", description = "게시물 관련 API")
public class PostController {
	private final PostService postService;

	@Operation(summary = "게시물 작성", description = "새로운 게시물을 작성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 작성 성공"),
		@ApiResponse(responseCode = "400", description = "게시물 작성 실패(Snapshot 데이터 문제)",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 400, \"error\": \"Bad Request\", \"message\": \"snapshot 데이터에 문제가 있습니다.\" }"
			))),
		@ApiResponse(responseCode = "404", description = "게시물 작성 실패(그룹을 찾을 수 없음)",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 그룹이 존재하지 않습니다.\" }"
			)))
	})
	@PostMapping("/post")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostInfo>> writePost(
		@Parameter(description = "그룹 ID") @PathVariable Long groupId,
		@Parameter(description = "게시물 내용") @RequestBody @Valid PostRequestDto.Write write,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) throws
		JsonProcessingException {
		return ResponseEntity.ok(
			CustomApiResponse.success(postService.save(groupId, write, customUserDetails.getUser())));
	}

	@Operation(summary = "게시물 상세 조회", description = "특정 게시물을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 조회 성공"),
		@ApiResponse(responseCode = "404", description = "게시물 조회 실패",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "글 없음", value = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 글이 존재하지 않습니다.\" }"),
				@ExampleObject(name = "그룹 내 글 없음", value = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"글이 해당 그룹에 있지 않습니다.\" }")
			}))
	})
	@GetMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostDetail>> getPost(
		@Parameter(description = "그룹 ID") @PathVariable Long groupId,
		@Parameter(description = "게시물 ID") @PathVariable Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {

		return ResponseEntity.ok(
			CustomApiResponse.success(postService.getPost(groupId, postId, customUserDetails.getUser())));
	}

	@Operation(summary = "게시물 목록 조회", description = "페이지별로 게시물 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공")
	@GetMapping("/post")
	public ResponseEntity<CustomApiResponse<List<PostResponseDto.PostInfo>>> getPostsByPage(
		@Parameter(description = "그룹 ID") @PathVariable Long groupId,
		@Parameter(description = "페이지 번호") @RequestParam(required = false, defaultValue = "0") int page,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(
			CustomApiResponse.success(postService.getPostsByPage(groupId, page, customUserDetails.getUser())));
	}

	@Operation(summary = "게시물 수정", description = "특정 게시물을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 수정 성공"),
		@ApiResponse(responseCode = "401", description = "게시물 수정 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"해당 글에 권한이 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "404", description = "게시물 수정 실패",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "", value = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 글이 존재하지 않습니다.\" }"),
				@ExampleObject(name = "", value = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 그룹에 권한이 없습니다.\" }")
			}))
	})
	@PatchMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostInfo>> updatePost(
		@Parameter(description = "그룹 ID") @PathVariable Long groupId,
		@Parameter(description = "게시물 ID") @PathVariable Long postId,
		@Parameter(description = "수정할 게시물 내용") @RequestBody @Valid PostRequestDto.Write write,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) throws
		JsonProcessingException {

		return ResponseEntity.ok(
			CustomApiResponse.success(postService.updatePost(groupId, postId, write, customUserDetails.getUser())));
	}

	@Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "게시물 삭제 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"해당 글에 권한이 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "404", description = "게시물 삭제 실패",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "글 없음", value = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 글이 존재하지 않습니다.\" }"),
				@ExampleObject(name = "그룹 권한 없음", value = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 그룹에 권한이 없습니다.\" }")
			}))
	})
	@DeleteMapping("/post/{postId}")
	public ResponseEntity<CustomApiResponse<PostResponseDto.PostInfo>> deletePost(
		@Parameter(description = "그룹 ID") @PathVariable Long groupId,
		@Parameter(description = "게시물 ID") @PathVariable Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {

		postService.deletePost(groupId, postId, customUserDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success());
	}
}
