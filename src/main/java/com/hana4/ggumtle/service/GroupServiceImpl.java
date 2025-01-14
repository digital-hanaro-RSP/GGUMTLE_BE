package com.hana4.ggumtle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.CreateGroupRequestDto;
import com.hana4.ggumtle.dto.GroupResponseDto;
import com.hana4.ggumtle.dto.JoinGroupRequestDto;
import com.hana4.ggumtle.dto.JoinGroupSuccessResponseDto;
import com.hana4.ggumtle.exception.DuplicateResourceException;
import com.hana4.ggumtle.exception.ResourceNotFoundException;
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
public class GroupServiceImpl implements GroupService {
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	@Override
	public GroupResponseDto createGroup(CreateGroupRequestDto request) throws Exception {
		Group group = Group.builder()
			.name(request.getName())
			.category(GroupCategory.valueOf(request.getCategory()))
			.description(request.getDescription())
			.imageUrl(request.getImageUrl())
			.build();
		groupRepository.save(group);
		return GroupResponseDto.builder()
			.id(group.getId())
			.name(group.getName())
			.category(String.valueOf(group.getCategory()))
			.description(group.getDescription())
			.imageUrl(group.getImageUrl())
			.build();

	}

	@Override
	public List<GroupResponseDto> getAllGroups() {
		List<Group> groups = groupRepository.findAll();
		return groups.stream()
			.map(group -> GroupResponseDto.builder()
				.id(group.getId())
				.name(group.getName())
				.category(String.valueOf(group.getCategory()))
				.description(group.getDescription())
				.imageUrl(group.getImageUrl())
				.createdAt(group.getCreatedAt())
				.updatedAt(group.getUpdatedAt())
				.build())
			.collect(Collectors.toList());
	}

	public void deleteGroup(Long groupId) {
		groupRepository.deleteById(groupId);
	}

	public JoinGroupSuccessResponseDto joinGroup(Long groupId, JoinGroupRequestDto request) {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new ResourceNotFoundException("Group not found"));

		User user = userRepository.findById(String.valueOf(request.getUserId()))
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (groupMemberRepository.existsByGroupAndUser(group, user)) {
			throw new DuplicateResourceException("User is already a member of this group.");
		}

		GroupMember groupMember = GroupMember.builder()
			.group(group)
			.user(user)
			.build();
		groupMemberRepository.save(groupMember);

		JoinGroupSuccessResponseDto.JoinGroupDataDto data = JoinGroupSuccessResponseDto.JoinGroupDataDto.builder()
			.id(String.valueOf(groupMember.getId()))
			.groupId(groupId)
			.userId(Long.valueOf(groupMember.getUser().getId()))
			.build();

		return new JoinGroupSuccessResponseDto(201, null, "ok", data);
	}
}
