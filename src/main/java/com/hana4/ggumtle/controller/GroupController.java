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
import com.hana4.ggumtle.dto.group.GroupRequestDto;
import com.hana4.ggumtle.dto.group.GroupResponseDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberRequestDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.GroupService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/community/group")
@RequiredArgsConstructor
@Tag(name = "Group", description = "커뮤니티 그룹 관련 API")
public class GroupController {
	private final GroupService groupService;

	@Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "그룹 생성 성공"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 500, \"error\": \"Internal_Server_Error\", \"message\": \"그룹이 생성되지 않았어요!(내부 서버 오류)\" }"
			)))
	})
	@PostMapping
	public ResponseEntity<CustomApiResponse<GroupResponseDto.Create>> createGroup(
		@RequestBody @Valid GroupRequestDto.Create request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		try {
			GroupResponseDto.Create response = groupService.createGroup(request, userDetails.getUser());
			return ResponseEntity.ok(CustomApiResponse.success(response));
		} catch (CustomException ce) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "모든 그룹 조회", description = "모든 그룹을 조회합니다. 그룹 내 멤버 수도 함께 제공됩니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "그룹 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 400, \"error\": \"BadRequest\", \"message\": \"해당 그룹이 존재하지 않습니다.\" }"
			)))
	})
	@GetMapping
	public ResponseEntity<Page<GroupResponseDto.Read>> getAllGroups(
		@RequestParam(required = false) GroupCategory category,
		@RequestParam(required = false) String search,
		@RequestParam(defaultValue = "0") int offset,
		@RequestParam(defaultValue = "10") int limit
	) {
		if (limit <= 0) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		Pageable pageable = PageRequest.of(offset / limit, limit);
		Page<GroupResponseDto.Read> groups = groupService.getAllGroupsWithMemberCount(category, search, pageable);
		return ResponseEntity.ok(groups);
	}

	@Operation(summary = "그룹 가입", description = "특정 커뮤니티 그룹에 가입합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "그룹 가입 성공"),
		@ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 404, \"error\": \"NotFound\", \"message\": \"가입 할 그룹이 존재하지 않습니다.\" }"
			)))
	})
	@PostMapping("/{groupId}/member")
	public ResponseEntity<CustomApiResponse<GroupMemberResponseDto.JoinGroup>> joinGroup(@PathVariable Long groupId,
		@RequestBody @Valid
		GroupMemberRequestDto.Create request, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			GroupMemberResponseDto.JoinGroup response = groupService.joinGroup(groupId, request, userDetails.getUser());
			return ResponseEntity.ok(CustomApiResponse.success(response));
		} catch (CustomException ce) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
	}

	@Operation(summary = "그룹 탈퇴", description = "그룹에서 탈퇴합니다. 마지막 멤버가 탈퇴하면 그룹이 자동으로 삭제됩니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "그룹 탈퇴 성공"),
		@ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(
				example = "{ \"code\": 409, \"error\": \"NotFound\", \"message\": \"그룹이 존재하지 않습니다.\" }"
			)))
	})
	@DeleteMapping("/{groupId}/member")
	public ResponseEntity<CustomApiResponse<GroupMemberResponseDto.LeaveGroup>> leaveGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			GroupMemberResponseDto.LeaveGroup response = groupService.leaveGroup(groupId, userDetails.getUser());
			return ResponseEntity.ok(CustomApiResponse.success(response));
		} catch (CustomException ce) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
	}
}
