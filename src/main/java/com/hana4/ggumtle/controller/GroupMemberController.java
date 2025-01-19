package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.GroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/community/groupMember")
@RequiredArgsConstructor
@Tag(name = "Group", description = "커뮤니티 그룹 멤버 관련 API")
public class GroupMemberController {
	private final GroupService groupService;

	@Operation(summary = "그룹 내 멤버 여부 조회", description = "현재 로그인 한 사용자가 그룹 내 멤버인지 여부를 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "그룹 내 멤버 여부 반환 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 400, \"error\": \"BadRequest\", \"message\": \"잘못된 파라미터입니다.\" }"
			))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 401, \"error\": \"Unauthorized\", \"message\": \"인증이 필요합니다.\" }"
			))),
		@ApiResponse(
			responseCode = "404",
			description = "그룹이 존재하지 않음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"GroupNotFound\", \"message\": \"그룹을 찾을 수 없습니다.\" }"
			))
		)
	})
	@GetMapping("/exists")
	public ResponseEntity<CustomApiResponse<Boolean>> isMemberOfGroup(
		@Parameter(description = "조회할 그룹의 ID", required = true, example = "1")
		@RequestParam Long groupId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails // 헤더에서 사용자 정보 추출
	) {
		// 사용자 정보 추출
		User user = customUserDetails.getUser();

		// 해당 그룹에 사용자가 가입되어 있는지 여부 확인
		boolean isMember = groupService.isMemberOfGroup(groupId, user.getId());

		return ResponseEntity.ok(CustomApiResponse.success(isMember));
	}
}
