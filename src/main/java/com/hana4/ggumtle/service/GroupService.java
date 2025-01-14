package com.hana4.ggumtle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.group.GroupRequestDto;
import com.hana4.ggumtle.dto.group.GroupResponseDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberRequestDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
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
public class GroupService {
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	public GroupResponseDto.Create createGroup(GroupRequestDto.Create request) {
		Group group = request.toEntity();

		return GroupResponseDto.Create.from(groupRepository.save(group));
	}

	public GroupResponseDto.Read toReadDto(Group group) {
		// 멤버 수를 계산하여 DTO에 포함
		int memberCount = groupMemberRepository.countByGroup(group);
		return GroupResponseDto.Read.from(group, memberCount);
	}

	public Page<GroupResponseDto.Read> getAllGroupsWithMemberCount(Pageable pageable) {
		// 페이징 처리된 Group 가져오기
		Page<Group> groups = groupRepository.findAll(pageable);

		// 각 Group에 대해 DTO 변환 및 멤버 수 계산
		return groups.map(group -> {
			int memberCount = groupMemberRepository.countByGroup(group);
			return GroupResponseDto.Read.from(group, memberCount);
		});
	}

	public void deleteGroup(Long groupId) {
		groupRepository.deleteById(groupId);
	}

	public GroupMemberResponseDto.JoinGroup joinGroup(Long groupId, GroupMemberRequestDto.Create request) {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		User user = userRepository.findById(String.valueOf(request.getUserId()))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		if (groupMemberRepository.existsByGroupAndUser(group, user)) {
			throw new CustomException(ErrorCode.ALREADY_EXISTS);
		}

		GroupMember groupMember = request.toEntity();
		groupMember.setGroup(group);
		groupMember.setUser(user);

		GroupMemberResponseDto.JoinGroup data = GroupMemberResponseDto.JoinGroup.from(
			groupMemberRepository.save(groupMember));

		return data;
	}
}
