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

	/**
	 * 새로운 그룹을 생성하고 생성자를 멤버로 추가함.
	 *
	 * @param request 그룹 생성에 필요한 정보를 담은 DTO
	 * @param user 그룹을 생성하는 사용자 객체
	 * @return 생성된 그룹 정보와 초기 멤버 수(1)를 포함한 Group객체
	 * @throws CustomException 그룹 생성 중 오류가 발생할 경우
	 */
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

	/**
	 * 모든 그룹을 조회(그룹 내 멤버 COUNT)
	 *
	 * @param category 조회할 그룹의 카테고리 (선택)
	 * @param search 그룹 이름 검색어 (선택)
	 * @param pageable 페이징 정보
	 * @return 그룹 정보와 멤버 수를 포함한 페이지
	 */
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

	/**
	 * 내 그룹 조회
	 *
	 * @param userId 조회할 사용자의 ID
	 * @param category 조회할 그룹의 카테고리 (선택적)
	 * @param search 그룹 이름 검색어 (선택적)
	 * @param pageable 페이징 정보
	 * @return 사용자가 가입한 그룹 정보와 멤버 수를 포함한 페이지
	 */
	public Page<GroupResponseDto.Read> getMyGroupsWithMemberCount(String userId, GroupCategory category, String search,
		Pageable pageable) {
		Page<Object[]> results = groupRepository.findGroupsWithFilters(userId, category, search, pageable);

		return results.map(result -> {
			Group group = (Group)result[0];
			Long memberCount = (Long)result[1];

			return GroupResponseDto.Read.from(group, memberCount.intValue());
		});
	}

	/**
	 * 사용자가 특정 그룹에 가입하도록 하는 매서드.
	 *
	 * @param groupId 가입할 그룹의 ID
	 * @param user 탈퇴하려는 사용자 객체
	 * @return 그룹 가입 결과를 담은 객체
	 * @throws CustomException 다음의 경우에 발생:
	 *         - 그룹을 찾을 수 없을 경우 (ErrorCode.NOT_FOUND)
	 *         - 사용자가 그룹의 멤버가 아닌 경우 (ErrorCode.NOT_FOUND)
	 * @implNote 마지막 멤버가 탈퇴할 경우 해당 그룹은 자동으로 삭제됩니다.
	 */
	public GroupMemberResponseDto.LeaveGroup leaveGroup(Long groupId, User user) {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		// 그룹 멤버 삭제
		GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		GroupMemberResponseDto.LeaveGroup response = GroupMemberResponseDto.LeaveGroup.from(groupMember);

		groupMemberRepository.delete(groupMember);

		// 멤버 수 확인 후 그룹 자동 삭제
		int remainingMembers = groupMemberRepository.countByGroup(group);
		if (remainingMembers == 0) {
			groupRepository.delete(group);
		}
		return response;
	}

	/**
	 * 사용자가 특정 그룹에 가입하도록 하는 매서드.
	 *
	 * @param groupId 가입할 그룹의 ID
	 * @param request 그룹가입 요청정보
	 * @param user 가입하려는 사용자 객체
	 * @return 그룹 가입 결과를 담은 객체
	 * @throws CustomException 에러 코드와 함께 발생
	 * 		- 그룹을 찾을 수 없을 경우 (ErrorCode.NOT_FOUND)
	 * 		- 사용자가 이미 그룹의 멤버일 경우 (ErrorCode.ALREADY_EXISTS)
	 */
	public GroupMemberResponseDto.JoinGroup joinGroup(Long groupId, GroupMemberRequestDto.CreateGroupMember request,
		User user) {
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

	/**
	 * 사용자가 특정 그룹의 멤버인지 여부를 확인합니다.
	 *
	 * @param groupId 확인할 그룹의 ID
	 * @param id 확인할 사용자의 ID
	 * @return 사용자가 그룹의 멤버이면 true, 아니면 false
	 * @throws CustomException 그룹을 찾을 수 없을 경우 GROUP_NOT_FOUND 에러 코드와 함께 발생
	 */
	public boolean isMemberOfGroup(Long groupId, String id) {
		Group group = groupRepository.findById(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

		User user = new User();
		user.setId(id);

		return groupMemberRepository.existsByGroupAndUser(group, user);
	}
}
