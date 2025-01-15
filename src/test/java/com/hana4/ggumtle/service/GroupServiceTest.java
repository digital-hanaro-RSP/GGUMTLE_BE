package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.group.GroupRequestDto;
import com.hana4.ggumtle.dto.group.GroupResponseDto;
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
		//given
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

		// when
		GroupResponseDto.Create expectedResponse = GroupResponseDto.Create.from(mockGroup, 1);
		GroupResponseDto.Create actualResponse = groupService.createGroup(request, mockUser);

		// then
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
}
