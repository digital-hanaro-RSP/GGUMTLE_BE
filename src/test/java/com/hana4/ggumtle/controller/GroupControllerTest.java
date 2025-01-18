package com.hana4.ggumtle.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
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
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.security.filter.JwtAuthFilter;
import com.hana4.ggumtle.security.provider.JwtProvider;
import com.hana4.ggumtle.service.CustomUserDetailsService;
import com.hana4.ggumtle.service.GroupService;

@WebMvcTest(GroupController.class)
@WithMockCustomUser
@Import(TestSecurityConfig.class)
class GroupControllerTest {
	@MockitoBean
	GroupService groupService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	JwtAuthFilter jwtAuthFilter;

	@MockitoBean
	JwtProvider jwtProvider;

	@MockitoBean
	CustomUserDetailsService customUserDetailsService;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity()) // Spring Security 통합
			.build();
	}

	@Test
	void createGroup() throws Exception {
		// given
		GroupRequestDto.Create request = GroupRequestDto.Create.builder()
			.name("Study Group")
			.category(GroupCategory.EDUCATION)
			.description("A group for studying together")
			.imageUrl("http://example.com/image.jpg")
			.build();

		GroupResponseDto.Create response = GroupResponseDto.Create.builder()
			.id(1L)
			.name("Study Group")
			.category(GroupCategory.EDUCATION)
			.description("A group for studying together")
			.imageUrl("http://example.com/image.jpg")
			.memberCount(1)
			.build();

		given(groupService.createGroup(any(GroupRequestDto.Create.class), any(User.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(post("/community/group")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.name").value("Study Group"))
			.andExpect(jsonPath("$.data.category").value("EDUCATION"))
			.andExpect(jsonPath("$.data.description").value("A group for studying together"))
			.andExpect(jsonPath("$.data.imageUrl").value("http://example.com/image.jpg"))
			.andExpect(jsonPath("$.data.memberCount").value(1))
			.andDo(print());

		verify(groupService).createGroup(any(GroupRequestDto.Create.class), any(User.class));
	}

	@Test
	void createGroup_내부서버오류() throws Exception {
		// given
		GroupRequestDto.Create request = GroupRequestDto.Create.builder()
			.name("Study Group")
			.category(GroupCategory.EDUCATION)
			.description("A group for studying together")
			.imageUrl("http://example.com/image.jpg")
			.build();

		given(groupService.createGroup(any(GroupRequestDto.Create.class), any(User.class)))
			.willThrow(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

		// when & then
		mockMvc.perform(post("/community/group")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
			.andDo(print());

		verify(groupService).createGroup(any(GroupRequestDto.Create.class), any(User.class));
	}

	@Test
	void getAllGroups() throws Exception {
		// given
		GroupCategory category = GroupCategory.HOBBY;
		String search = "test";
		int offset = 0;
		int limit = 10;

		GroupResponseDto.Read groupDto = GroupResponseDto.Read.builder()
			.id(1L)
			.name("Test Group")
			.category(category)
			.description("Test Description")
			.imageUrl("http://example.com/image.jpg")
			.memberCount(5)
			.build();

		Page<GroupResponseDto.Read> groupPage = new PageImpl<>(List.of(groupDto));

		given(groupService.getAllGroupsWithMemberCount(eq(category), eq(search), any(Pageable.class)))
			.willReturn(groupPage);

		// when & then
		mockMvc.perform(get("/community/group")
				.param("category", category.name())
				.param("search", search)
				.param("offset", String.valueOf(offset))
				.param("limit", String.valueOf(limit)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content[0].id").value(1L))
			.andExpect(jsonPath("$.data.content[0].name").value("Test Group"))
			.andExpect(jsonPath("$.data.content[0].category").value(category.name()))
			.andExpect(jsonPath("$.data.content[0].description").value("Test Description"))
			.andExpect(jsonPath("$.data.content[0].imageUrl").value("http://example.com/image.jpg"))
			.andExpect(jsonPath("$.data.content[0].memberCount").value(5))
			.andDo(print());

		verify(groupService).getAllGroupsWithMemberCount(eq(category), eq(search), any(Pageable.class));

	}

	@Test
	void getAllGroups_InvalidLimit() throws Exception {
		// given
		GroupCategory category = GroupCategory.HOBBY;
		String search = "test";
		int offset = 0;
		int limit = 0;

		// when & then
		mockMvc.perform(get("/community/group")
				.param("category", category.name())
				.param("search", search)
				.param("offset", String.valueOf(offset))
				.param("limit", String.valueOf(limit)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()))
			.andDo(print());
	}

	@Test
	void getMyGroups_성공() throws Exception {
		// given
		String userId = "1";
		GroupCategory category = GroupCategory.HOBBY;
		String search = "투자";
		int offset = 0;
		int limit = 20;

		GroupResponseDto.Read dto = GroupResponseDto.Read.builder()
			.id(1L)
			.name("투자 모임")
			.category(GroupCategory.HOBBY)
			.description("투자 정보 공유")
			.imageUrl("http://example.com/image.jpg")
			.memberCount(5)
			.build();

		Page<GroupResponseDto.Read> page = new PageImpl<>(List.of(dto));

		when(groupService.getMyGroupsWithMemberCount(eq(userId), eq(category), eq(search), any(Pageable.class)))
			.thenReturn(page);

		CustomUserDetails userDetails = new CustomUserDetails(User.builder().id(userId).build());

		// when & then
		mockMvc.perform(get("/community/group/my-group")
				.param("category", category.name())
				.param("search", search)
				.param("offset", String.valueOf(offset))
				.param("limit", String.valueOf(limit))
				.with(user(userDetails)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content[0].id").value(1L))
			.andExpect(jsonPath("$.data.content[0].name").value("투자 모임"))
			.andExpect(jsonPath("$.data.content[0].category").value(category.name()))
			.andExpect(jsonPath("$.data.content[0].description").value("투자 정보 공유"))
			.andExpect(jsonPath("$.data.content[0].imageUrl").value("http://example.com/image.jpg"))
			.andExpect(jsonPath("$.data.content[0].memberCount").value(5))
			.andDo(print());

	}

	@Test
	void getMyGroups_InvalidLimit() throws Exception {
		// given
		String userId = "1";
		GroupCategory category = GroupCategory.HOBBY;
		String search = "투자";
		int offset = 0;
		int limit = 0;

		CustomUserDetails userDetails = new CustomUserDetails(User.builder().id(userId).build());

		// when & then
		mockMvc.perform(get("/community/group/my-group")
				.param("category", category.name())
				.param("search", search)
				.param("offset", String.valueOf(offset))
				.param("limit", String.valueOf(limit))
				.with(user(userDetails)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()))
			.andDo(print());
	}

	@Test
	void joinGroup() throws Exception {
		// given
		Long groupId = 1L;
		GroupMemberRequestDto.Create request = new GroupMemberRequestDto.Create(groupId);

		Group group = new Group();
		group.setId(groupId);
		group.setName("Study Group");

		User user = User.builder()
			.id("user-uuid")
			.name("Test User")
			.build();

		GroupMemberResponseDto.JoinGroup response = GroupMemberResponseDto.JoinGroup.builder()
			.id(1L)
			.groupId(group)
			.userId(user)
			.build();

		given(groupService.joinGroup(eq(groupId), any(GroupMemberRequestDto.Create.class), any(User.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(post("/community/group/{groupId}/member", groupId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.groupId.id").value(groupId))
			.andExpect(jsonPath("$.data.groupId.name").value("Study Group"))
			.andExpect(jsonPath("$.data.userId.id").value("user-uuid"))
			.andExpect(jsonPath("$.data.userId.name").value("Test User"))
			.andDo(print());

		verify(groupService).joinGroup(eq(groupId), any(GroupMemberRequestDto.Create.class), any(User.class));
	}

	@Test
	void joinGroup_from_ShouldMapCorrectly() {
		// given
		Group group = new Group();
		group.setId(1L);
		group.setName("Study Group");

		User user = new User();
		user.setId("user-uuid");
		user.setName("Test User");

		LocalDateTime now = LocalDateTime.now();
		GroupMember groupMember = new GroupMember();
		groupMember.setId(1L);
		groupMember.setGroup(group);
		groupMember.setUser(user);
		groupMember.setCreatedAt(now);
		groupMember.setUpdatedAt(now);

		// when
		GroupMemberResponseDto.JoinGroup result = GroupMemberResponseDto.JoinGroup.from(groupMember);

		// then
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals(group, result.getGroupId());
		assertEquals(user, result.getUserId());
		assertEquals(now, result.getCreatedAt());
		assertEquals(now, result.getUpdatedAt());
	}

	@Test
	void joinGroup_NotFound() throws Exception {
		// given
		Long groupId = 1L;
		GroupMemberRequestDto.Create request = new GroupMemberRequestDto.Create(groupId);

		User user = User.builder()
			.id("user-uuid")
			.name("Test User")
			.build();

		CustomUserDetails userDetails = new CustomUserDetails(user);

		given(customUserDetailsService.loadUserByUsername("testUser")).willReturn(userDetails);

		given(groupService.joinGroup(eq(groupId), any(GroupMemberRequestDto.Create.class), any(User.class)))
			.willThrow(new CustomException(ErrorCode.NOT_FOUND));

		// when & then
		mockMvc.perform(post("/community/group/{groupId}/member", groupId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(user(userDetails)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND.getMessage()))
			.andDo(print());

		verify(groupService).joinGroup(eq(groupId), any(GroupMemberRequestDto.Create.class), any(User.class));
	}

	@Test
	void leaveGroup_성공() throws Exception {
		// given
		Long groupId = 1L;
		User user = User.builder()
			.id("user-uuid")
			.name("Test User")
			.build();

		Group group = new Group();
		group.setId(groupId);
		group.setName("Test Group");

		GroupMember groupMember = new GroupMember();
		groupMember.setId(1L);
		groupMember.setGroup(group);
		groupMember.setUser(user);

		GroupMemberResponseDto.LeaveGroup response = GroupMemberResponseDto.LeaveGroup.from(groupMember);

		given(groupService.leaveGroup(eq(groupId), any(User.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(delete("/community/group/{groupId}/member", groupId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.groupId.id").value(groupId))
			.andExpect(jsonPath("$.data.groupId.name").value("Test Group"))
			.andExpect(jsonPath("$.data.userId.id").value("user-uuid"))
			.andExpect(jsonPath("$.data.userId.name").value("Test User"))
			.andDo(print());

		verify(groupService).leaveGroup(eq(groupId), any(User.class));
	}

	@Test
	void leaveGroup_그룹없음() throws Exception {
		// given
		Long groupId = 1L;

		given(groupService.leaveGroup(eq(groupId), any(User.class)))
			.willThrow(new CustomException(ErrorCode.NOT_FOUND));

		// when & then
		mockMvc.perform(delete("/community/group/{groupId}/member", groupId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND.getMessage()))
			.andDo(print());

		verify(groupService).leaveGroup(eq(groupId), any(User.class));
	}
}