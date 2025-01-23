package com.hana4.ggumtle.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.comment.CommentLikeResponseDto;
import com.hana4.ggumtle.dto.comment.CommentRequestDto;
import com.hana4.ggumtle.dto.comment.CommentResponseDto;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/")
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {
	private final CommentService commentService;

	@Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
		@ApiResponse(responseCode = "404", description = "댓글 작성 실패(해당 글을 찾을 수 없음)",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 글이 존재하지 않습니다.\" }"
			)))
	})
	@PostMapping("/post/{postId}/comment")
	public ResponseEntity<CustomApiResponse<CommentResponseDto.CommentInfo>> addComment(
		@Parameter(description = "Post ID") @PathVariable Long postId,
		@Parameter(description = "댓글 내용") @RequestBody @Valid CommentRequestDto.CommentWrite commentWrite,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(CustomApiResponse.success(
			commentService.saveComment(postId, commentWrite, customUserDetails.getUser())));
	}

	@Operation(summary = "댓글 목록 조회", description = "페이지별로 댓글 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
	@GetMapping("/post/{postId}/comments")
	public ResponseEntity<CustomApiResponse<Page<CommentResponseDto.CommentInfo>>> getComments(
		@Parameter(description = "페이지 번호") @RequestParam(required = false, defaultValue = "0") int offset,
		@Parameter(description = "페이지 번호") @RequestParam(required = false, defaultValue = "10") int limit,
		@Parameter(description = "Post ID") @PathVariable Long postId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Pageable pageable = PageRequest.of(offset / limit, limit);
		return ResponseEntity.ok(CustomApiResponse.success(
			commentService.getCommentsByPage(postId, pageable, customUserDetails.getUser())));
	}

	@Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "댓글 삭제 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"해당 댓글에 권한이 없습니다.\" }"
			))),
		@ApiResponse(responseCode = "404", description = "댓글 삭제 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 댓글이 존재하지 않습니다.\" }"
			)))
	})
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<CustomApiResponse<Void>> deleteComment(
		@Parameter(description = "댓글 ID") @PathVariable Long commentId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.ok(CustomApiResponse.success());
	}

	@Operation(summary = "댓글 좋아요 추가", description = "특정 댓글에 좋아요를 추가합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "좋아요 추가 성공"),
		@ApiResponse(responseCode = "404", description = "좋아요 추가 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 댓글이 존재하지 않습니다.\" }"
			)))
	})
	@PostMapping("/comment/{commentId}/like")
	public ResponseEntity<CustomApiResponse<CommentLikeResponseDto.CommentLikeInfo>> likeComment(
		@Parameter(description = "댓글 ID") @PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return ResponseEntity.ok(
			CustomApiResponse.success(commentService.addLike(commentId, customUserDetails.getUser())));
	}

	@Operation(summary = "댓글 좋아요 취소", description = "특정 댓글의 좋아요를 취소합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
		@ApiResponse(responseCode = "404", description = "좋아요 취소 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"Not Found\", \"message\": \"해당 댓글이 존재하지 않습니다.\" }"
			)))
	})
	@DeleteMapping("/comment/{commentId}/dislike")
	public ResponseEntity<CustomApiResponse<Void>> dislikeComment(
		@Parameter(description = "댓글 ID") @PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		commentService.removeLike(commentId, customUserDetails.getUser());
		return ResponseEntity.ok(CustomApiResponse.success());
	}
}
