package com.hana4.ggumtle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.model.dto.CreateGroupRequestDto;
import com.hana4.ggumtle.model.dto.GroupResponseDto;
import com.hana4.ggumtle.model.dto.JoinGroupRequestDto;
import com.hana4.ggumtle.model.dto.JoinGroupSuccessResponseDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.GroupMemberRepository;
import com.hana4.ggumtle.repository.GroupRepository;
import com.hana4.ggumtle.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService{
		private final GroupRepository groupRepository;
		private final GroupMemberRepository groupMemberRepository;
		private final UserRepository userRepository;
		private final ObjectMapper objectMapper;
		@Override
		public GroupResponseDto createGroup(CreateGroupRequestDto request) throws Exception {
				Group group = new Group(request.getName(), request.getCategory(), request.getDescription(), request.getImageUrl());
				groupRepository.save(group);
				return null; //todo /*new GroupResponseDto(group);*/
		}

		@Override
		public List<GroupResponseDto> getAllGroups() {
				List<Group> groups = groupRepository.findAll();
				return null;/* todo groups.stream().map(GroupResponseDto::new).collect(Collectors.toList());*/
		}
		//todo 그룹 내 인원이 0명이면 자동으로 그룹삭제
		public void deleteGroup(Long groupId) {
				groupRepository.deleteById(groupId);
		}

		// public JoinGroupSuccessResponseDto joinGroup(Long groupId, JoinGroupRequestDto request) {
		// 		Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
		// 		User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		//
		// 		if (groupMemberRepository.existsByGroupAndUser(group, user)) {
		// 				throw new DuplicateResourceException("User is already a member of this group.");
		// 		}
		//
		// 		GroupMember groupMember = new GroupMember(group, user);
		// 		groupMemberRepository.save(groupMember);
		//
		// 		return new JoinGroupSuccessResponseDto(201, null, "ok", new JoinGroupSuccessResponseDto.JoinGroupDataDto(groupMember));
		// }
}
