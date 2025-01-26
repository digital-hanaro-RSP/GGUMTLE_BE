package com.hana4.ggumtle.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

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
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.GroupMemberRepository;
import com.hana4.ggumtle.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
	@InjectMocks
	private GroupService groupService;

	@Mock
	private GroupRepository groupRepository;

	@Mock
	private GroupMemberRepository groupMemberRepository;

	@Test
	void createGroup_성공() {
		Group mockGroup = Group.builder()
			.id(1L)
			.name("타이거우즈")
			.category(GroupCategory.HOBBY)
			.description("내 꿈은 골프왕!")
			.imageUrl("http://example.com/image.png")
			.build();

		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("김도희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1996, 9, 9, 0, 0))
			.gender("W")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("telletobinana")
			.build();

		GroupRequestDto.Create request = GroupRequestDto.Create.from(mockGroup);

		when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);

		GroupResponseDto.CreateGroup expectedResponse = GroupResponseDto.CreateGroup.from(mockGroup, 1);
		GroupResponseDto.CreateGroup actualResponse = groupService.createGroup(request, mockUser);

		assertNotNull(actualResponse);
		assertEquals(expectedResponse.getId(), actualResponse.getId());
		assertEquals(expectedResponse.getName(), actualResponse.getName());
		assertEquals(expectedResponse.getCategory(), actualResponse.getCategory());
		assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
		assertEquals(expectedResponse.getImageUrl(), actualResponse.getImageUrl());
		assertEquals(expectedResponse.getMemberCount(), actualResponse.getMemberCount());

		verify(groupRepository, times(1)).save(any(Group.class));
		verify(groupMemberRepository, times(1)).save(any(GroupMember.class));
	}

	@Test
	void getAllGroupsWithMemberCount_성공() {
		GroupCategory category = GroupCategory.HOBBY;
		String search = null;  // 모든 그룹 조회를 위해 null로 설정
		Pageable pageable = PageRequest.of(0, 10);

		// mockGroup 생성
		Group mockGroup = Group.builder()
			.id(1L)
			.name("부자왕")
			.category(GroupCategory.INVESTMENT)
			.description("노후자금을 불려봅시다!")
			.imageUrl("http://example.com/image.png")
			.build();

		Object[] mockResult = new Object[] {mockGroup, 5L}; // 그룹과 멤버 수를 반환하는 결과
		Page<Object[]> mockPage = new PageImpl<>(Collections.singletonList(mockResult), pageable, 1);

		Mockito.when(groupRepository.findGroupsWithFilters(null, category, search, pageable))
			.thenReturn(mockPage);

		Page<GroupResponseDto.Read> result = groupService.getAllGroupsWithMemberCount(category, search, pageable);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		GroupResponseDto.Read dto = result.getContent().get(0);
		assertEquals(mockGroup.getName(), dto.getName());
		assertEquals(mockGroup.getCategory(), dto.getCategory());
		assertEquals(mockGroup.getDescription(), dto.getDescription());
		assertEquals(5, dto.getMemberCount());
	}

	@Test
	void getMyGroupsWithMemberCount_성공() {
		String userId = "test-user-id";
		GroupCategory category = GroupCategory.INVESTMENT;
		String search = "투자";
		Pageable pageable = PageRequest.of(0, 10);

		Group group = Group.builder()
			.id(1L)
			.name("투자 모임")
			.category(GroupCategory.INVESTMENT)
			.description("투자 정보 공유")
			.imageUrl("http://example.com/image.jpg")
			.build();

		Object[] mockResult = new Object[] {group, 1L};
		Page<Object[]> mockPage = new PageImpl<>(Collections.singletonList(mockResult), pageable, 1);

		when(groupRepository.findGroupsWithFilters(userId, category, search, pageable)).thenReturn(mockPage);

		Page<GroupResponseDto.Read> result = groupService.getMyGroupsWithMemberCount(userId, category, search,
			pageable);

		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		GroupResponseDto.Read dto = result.getContent().get(0);
		assertThat(dto.getId()).isEqualTo(group.getId());
		assertThat(dto.getName()).isEqualTo(group.getName());
		assertThat(dto.getMemberCount()).isEqualTo(1);
	}

	@Test
	void leaveGroup_성공() {
		Long groupId = 1L;

		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("김도희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1996, 9, 9, 0, 0))
			.gender("W")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("telletobinana")
			.build();

		Group mockGroup = Group.builder()
			.id(groupId)
			.name("부자왕")
			.category(GroupCategory.HOBBY)
			.description("노후자금을 불려봅시다!")
			.imageUrl("http://example.com/group-image.png")
			.build();

		GroupMember mockGroupMember = GroupMember.builder()
			.id(1L)
			.group(mockGroup)
			.user(mockUser)
			.build();

		Mockito.when(groupRepository.findById(groupId))
			.thenReturn(Optional.of(mockGroup));

		Mockito.when(groupMemberRepository.findByGroupAndUser(mockGroup, mockUser))
			.thenReturn(Optional.of(mockGroupMember));

		Mockito.when(groupMemberRepository.countByGroup(mockGroup))
			.thenReturn(0);

		GroupMemberResponseDto.LeaveGroup response = groupService.leaveGroup(groupId, mockUser);

		assertNotNull(response);
		assertEquals(mockGroupMember.getGroup(), response.getGroupId());
		assertEquals(mockGroupMember.getUser(), response.getUserId());

		Mockito.verify(groupMemberRepository, Mockito.times(1))
			.delete(mockGroupMember);

		Mockito.verify(groupRepository, Mockito.times(1))
			.delete(mockGroup);
	}

	@Test
	void leaveGroup_그룹없음_실패() {
		Long groupId = 1L;
		User mockUser = User.builder()
			.id("1")
			.name("김도희")
			.build();

		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		assertThrows(CustomException.class, () ->
			groupService.leaveGroup(groupId, mockUser));
	}

	@Test
	void leaveGroup_멤버없음_실패() {
		Long groupId = 1L;
		User mockUser = User.builder()
			.id("3")
			.name("김희도")
			.build();

		Group mockGroup = Group.builder()
			.id(groupId)
			.name("부자왕")
			.build();

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));
		when(groupMemberRepository.findByGroupAndUser(mockGroup, mockUser))
			.thenReturn(Optional.empty());

		assertThrows(CustomException.class, () ->
			groupService.leaveGroup(groupId, mockUser));
	}

	@Test
	void joinGroup_성공() {
		Long groupId = 1L;
		User mockUser = new User(
			"1",
			"010-5555-6666",
			"password",
			"김도희",
			(short)1,
			LocalDateTime.of(1996, 9, 12, 0, 0),
			"W",
			UserRole.USER,
			"https://example.com/profile.jpg",
			"telletobinana"
		);

		Group mockGroup = Group.builder()
			.id(groupId)
			.name("부자왕")
			.category(GroupCategory.INVESTMENT)
			.description("노후자금을 불려봅시다!")
			.imageUrl("http://example.com/image.png")
			.build();

		GroupMemberRequestDto.CreateGroupMember request = GroupMemberRequestDto.CreateGroupMember.builder()
			.groupId(groupId)
			.build();

		GroupMember mockGroupMember = GroupMember.builder()
			.id(1L)
			.group(mockGroup)
			.user(mockUser)
			.build();

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));
		when(groupMemberRepository.existsByGroupAndUser(mockGroup, mockUser)).thenReturn(false);
		when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(mockGroupMember);

		GroupMemberResponseDto.JoinGroup response = groupService.joinGroup(groupId, request, mockUser);

		assertNotNull(response);
		assertEquals(mockGroupMember.getId(), response.getId());
		assertEquals(mockGroup, response.getGroupId());
		assertEquals(mockUser, response.getUserId());

		verify(groupRepository, times(1)).findById(groupId);
		verify(groupMemberRepository, times(1)).existsByGroupAndUser(mockGroup, mockUser);
		verify(groupMemberRepository, times(1)).save(any(GroupMember.class));
	}

	@Test
	void joinGroup_그룹없음_실패() {
		Long groupId = 1L;
		User mockUser = new User(
			"1",
			"010-5555-6666",
			"password",
			"김도희",
			(short)1,
			LocalDateTime.of(1996, 9, 12, 0, 0),
			"W",
			UserRole.USER,
			"https://example.com/profile.jpg",
			"telletobinana"
		);

		GroupMemberRequestDto.CreateGroupMember request = GroupMemberRequestDto.CreateGroupMember.builder()
			.groupId(groupId)
			.build();

		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			groupService.joinGroup(groupId, request, mockUser));
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void joinGroup_이미존재하는멤버_실패() {
		Long groupId = 1L;
		User mockUser = new User(
			"1",
			"010-5555-6666",
			"password",
			"김도희",
			(short)1,
			LocalDateTime.of(1996, 9, 12, 0, 0),
			"W",
			UserRole.USER,
			"https://example.com/profile.jpg",
			"telletobinana"
		);

		Group mockGroup = Group.builder()
			.id(groupId)
			.name("부자왕")
			.category(GroupCategory.INVESTMENT)
			.description("노후자금을 불려봅시다!")
			.imageUrl("http://example.com/image.png")
			.build();

		GroupMemberRequestDto.CreateGroupMember request = GroupMemberRequestDto.CreateGroupMember.builder()
			.groupId(groupId)
			.build();

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));
		when(groupMemberRepository.existsByGroupAndUser(mockGroup, mockUser)).thenReturn(true);

		CustomException exception = assertThrows(CustomException.class, () ->
			groupService.joinGroup(groupId, request, mockUser));
		assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());
	}

	@Test
	@WithMockUser(username = "testUser")
	void leaveGroup_그룹자동삭제() {
		Long groupId = 1L;
		Group group = new Group();
		group.setId(groupId);

		User user = new User();
		user.setId("user-uuid");

		GroupMember groupMember = new GroupMember();
		groupMember.setGroup(group);
		groupMember.setUser(user);

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
		when(groupMemberRepository.findByGroupAndUser(group, user)).thenReturn(Optional.of(groupMember));
		when(groupMemberRepository.countByGroup(group)).thenReturn(0); // 마지막 멤버 시뮬레이션

		GroupMemberResponseDto.LeaveGroup result = groupService.leaveGroup(groupId, user);

		assertNotNull(result);
		verify(groupMemberRepository).delete(groupMember);
		verify(groupRepository).delete(group);
		verify(groupMemberRepository).countByGroup(group);

		verifyNoMoreInteractions(groupRepository, groupMemberRepository);
	}

	@Test
	void leaveGroup_마지막멤버가_아닐경우() {
		Long groupId = 1L;
		Group group = new Group();
		group.setId(groupId);

		User user = new User();
		user.setId("user-uuid");

		GroupMember groupMember = new GroupMember();
		groupMember.setGroup(group);
		groupMember.setUser(user);

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
		when(groupMemberRepository.findByGroupAndUser(group, user)).thenReturn(Optional.of(groupMember));
		when(groupMemberRepository.countByGroup(group)).thenReturn(1); // 마지막 멤버가 아님을 시뮬레이션

		GroupMemberResponseDto.LeaveGroup result = groupService.leaveGroup(groupId, user);

		assertNotNull(result);
		verify(groupMemberRepository).delete(groupMember);
		verify(groupMemberRepository).countByGroup(group);
		verify(groupRepository, never()).delete(group); // 그룹이 삭제되지 않아야 함

		verifyNoMoreInteractions(groupRepository, groupMemberRepository);
	}

	@Test
	void IsMemberOfGroup_성공() {
		Long groupId = 1L;
		String userId = "user123";
		when(groupRepository.existsById(groupId)).thenReturn(true);
		when(groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)).thenReturn(true);
		boolean result = groupService.isMemberOfGroup(groupId, userId);
		assertTrue(result);
		verify(groupRepository).existsById(groupId);
		verify(groupMemberRepository).existsByGroupIdAndUserId(groupId, userId);
	}

	@Test
	void IsMemberOfGroup_그룹에존재하지않는멤버() {
		Long groupId = 1L;
		String userId = "user123";
		when(groupRepository.existsById(groupId)).thenReturn(true);
		when(groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)).thenReturn(false);
		boolean result = groupService.isMemberOfGroup(groupId, userId);
		assertFalse(result);
		verify(groupRepository).existsById(groupId);
		verify(groupMemberRepository).existsByGroupIdAndUserId(groupId, userId);
	}

	@Test
	void IsMemberOfGroup_해당그룹없음() {
		Long groupId = 1L;
		String userId = "user123";
		when(groupRepository.existsById(groupId)).thenReturn(false);
		CustomException exception = assertThrows(CustomException.class, () -> {
			groupService.isMemberOfGroup(groupId, userId);
		});
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("찾는 그룹이 없습니다.", exception.getMessage());
		verify(groupRepository).existsById(groupId);
		verifyNoInteractions(groupMemberRepository);
	}
}
