package com.hana4.ggumtle.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.model.dto.CreateGroupRequestDto;
import com.hana4.ggumtle.model.dto.GroupResponseDto;
import com.hana4.ggumtle.service.GroupService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/community/group")
@RequiredArgsConstructor
@Tag(name = "Group", description = "커뮤니티 그룹 관련 API")
public class GroupController {
		private final GroupService groupService;

		// 그룹 생성
		@PostMapping
		public ResponseEntity<GroupResponseDto> createGroup(@RequestBody CreateGroupRequestDto request) {
				try {
						GroupResponseDto response = groupService.createGroup(request);
						return ResponseEntity.status(HttpStatus.CREATED).body(response);
				} catch (Exception e) {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				}
		}

		// 모든 그룹 조회
		@GetMapping
		public ResponseEntity<List<GroupResponseDto>> getAllGroups() {
				List<GroupResponseDto> response = groupService.getAllGroups();
				return ResponseEntity.ok(response);
		}
}
