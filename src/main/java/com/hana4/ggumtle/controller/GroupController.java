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

	// 모든 그룹 조회(그룹 내 멤버 count)
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

	// 커뮤니티 그룹 가입
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

	// 커뮤니티 그룹 탈퇴(memberCount = 0 이면 자동으로 그룹 삭제.)
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
