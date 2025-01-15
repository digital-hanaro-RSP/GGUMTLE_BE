package com.hana4.ggumtle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.group.GroupRequestDto;
import com.hana4.ggumtle.dto.group.GroupResponseDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberRequestDto;
import com.hana4.ggumtle.dto.groupMember.GroupMemberResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
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
@Transactional
public class GroupService {
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	//그룹 생성
	public GroupResponseDto.Create createGroup(GroupRequestDto.Create request) {
		Group group = request.toEntity();

		return GroupResponseDto.Create.from(groupRepository.save(group));
	}

	//모든 그룹 조회(그룹 내 멤버 count)
	public Page<GroupResponseDto.Read> getAllGroupsWithMemberCount(GroupCategory category, String search,
		Pageable pageable) {
		Page<Object[]> results = groupRepository.findGroupsWithMemberCount(category, search, pageable);

		System.out.println("results = " + results);

		// Object[] -> DTO 변환
		return results.map(result -> {
			Group group = (Group)result[0];
			Long memberCount = (Long)result[1];

			return GroupResponseDto.Read.from(group, memberCount.intValue());
		});
	}

	//그룹 내 인원이 0명이면 그룹 자동 삭제
	public void deleteGroup(Long groupId) {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		int memberCount = groupMemberRepository.countByGroup(group);

		if (memberCount == 0) {
			groupRepository.deleteById(groupId);
		} else {
			throw new IllegalStateException(
				"Cannot delete group with active members. Current member count: " + memberCount);
		}
	}

	public GroupMemberResponseDto.JoinGroup joinGroup(Long groupId, GroupMemberRequestDto.Create request, User user) {
		Group group = groupRepository.findById(groupId)
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
