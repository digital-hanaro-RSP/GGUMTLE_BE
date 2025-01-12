package com.hana4.ggumtle.controller;

import java.util.List;

import org.junit.jupiter.api.Tag;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/community/group")
@RequiredArgsConstructor
//todo 어노테이션 와이라노 @Tag(name = "Group", description = "커뮤니티 그룹 관련 API")
public class GroupController {
		private final GroupService groupService;

		// 그룹 생성
		@PostMapping
		public ResponseEntity<GroupResponseDto> createGroup(@RequestBody CreateGroupRequestDto request) {
				//todo GroupResponseDto response = groupService.createGroup(request);
				return null;/*ResponseEntity.status(HttpStatus.CREATED).body(response);*/
		}
		// 모든 그룹 조회
		@GetMapping
		public ResponseEntity<List<GroupResponseDto>> getAllGroups() {
				List<GroupResponseDto> response = groupService.getAllGroups();
				return ResponseEntity.ok(response);
		}
}
