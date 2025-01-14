package com.hana4.ggumtle.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.ApiResponse;
import com.hana4.ggumtle.dto.group.GroupRequestDto;
import com.hana4.ggumtle.dto.group.GroupResponseDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberRequestDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.service.GroupService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/community/group")
@RequiredArgsConstructor
@Tag(name = "Group", description = "커뮤니티 그룹 관련 API")
public class GroupController {
	private final GroupService groupService;

	// 그룹 생성
	@PostMapping
	public ResponseEntity<ApiResponse<GroupResponseDto.Create>> createGroup(
		@RequestBody @Valid GroupRequestDto.Create request) {
		try {
			GroupResponseDto.Create response = groupService.createGroup(request);
			return ResponseEntity.ok(ApiResponse.success(response));
		} catch (CustomException ce) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	// 모든 그룹 조회(그룹 내 멤버 count)
	@GetMapping
	public ResponseEntity<Page<GroupResponseDto.Read>> getAllGroups(
		@RequestParam int page,
		@RequestParam int size
	) {
		Page<GroupResponseDto.Read> groups = groupService.getAllGroupsWithMemberCount(PageRequest.of(page, size));
		return ResponseEntity.ok(groups);
	}

	// 커뮤니티 그룹 가입
	@PostMapping("/{groupId}/member")
	public ResponseEntity<ApiResponse<GroupMemberResponseDto.JoinGroup>> joinGroup(@PathVariable Long groupId,
		@RequestBody @Valid
		GroupMemberRequestDto.Create request) {
		try {
			GroupMemberResponseDto.JoinGroup response = groupService.joinGroup(groupId, request);
			return ResponseEntity.ok(ApiResponse.success(response));
		} catch (CustomException ce) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
	}
}
