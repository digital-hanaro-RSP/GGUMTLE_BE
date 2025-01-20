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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupService {
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;

	//그룹 생성
	@Transactional
	public GroupResponseDto.CreateGroup createGroup(GroupRequestDto.Create request, User user) {
		Group group = request.toEntity();
		Group savedGroup = groupRepository.save(group);

		// 그룹 생성자를 멤버로 추가
		GroupMember groupMember = GroupMember.builder()
			.group(savedGroup)
			.user(user)
			.build();
		groupMemberRepository.save(groupMember);

		// memberCount가 1인 상태로 응답 반환
		return GroupResponseDto.CreateGroup.from(savedGroup, 1);
	}

	//모든 그룹 조회(그룹 내 멤버 count)
	public Page<GroupResponseDto.Read> getAllGroupsWithMemberCount(GroupCategory category, String search,
		Pageable pageable) {
		Page<Object[]> results = groupRepository.findGroupsWithFilters(null, category, search, pageable);

		// Object[] -> DTO 변환
		return results.map(result -> {
			Group group = (Group)result[0];
			Long memberCount = (Long)result[1];

			return GroupResponseDto.Read.from(group, memberCount.intValue());
		});
	}

	//내 그룹 조회
	public Page<GroupResponseDto.Read> getMyGroupsWithMemberCount(String userId, GroupCategory category, String search,
		Pageable pageable) {
		Page<Object[]> results = groupRepository.findGroupsWithFilters(userId, category, search, pageable);

		return results.map(result -> {
			Group group = (Group)result[0];
			Long memberCount = (Long)result[1];

			return GroupResponseDto.Read.from(group, memberCount.intValue());
		});
	}

	public GroupMemberResponseDto.LeaveGroup leaveGroup(Long groupId, User user) {
		Group group = getGroup(groupId);

		// 그룹 멤버 삭제
		GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 그룹에 권한이 없습니다."));

		GroupMemberResponseDto.LeaveGroup response = GroupMemberResponseDto.LeaveGroup.from(groupMember);

		groupMemberRepository.delete(groupMember);

		// 멤버 수 확인 후 그룹 자동 삭제
		int remainingMembers = groupMemberRepository.countByGroup(group);
		if (remainingMembers == 0) {
			groupRepository.delete(group);
		}
		return response;
	}

	public GroupMemberResponseDto.JoinGroup joinGroup(Long groupId, GroupMemberRequestDto.CreateGroupMember request,
		User user) {
		Group group = getGroup(groupId);

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

	public Group getGroup(Long groupId) {
		return groupRepository.findById(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 그룹이 존재하지 않습니다."));
	}

	public boolean isMatchedGroupUser(User user, Group group) {
		return groupMemberRepository.findByGroupAndUser(group, user).isPresent();
	}
}
