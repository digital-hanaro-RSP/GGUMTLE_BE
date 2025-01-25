package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.security.filter.JwtAuthFilter;
import com.hana4.ggumtle.security.provider.JwtProvider;
import com.hana4.ggumtle.service.CustomUserDetailsService;
import com.hana4.ggumtle.service.GroupService;

@WebMvcTest(GroupMemberController.class)
@WithMockCustomUser
class GroupMemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GroupService groupService;

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
	@WithMockUser
	void isMemberOfGroup_멤버인_경우() throws Exception {
		Long groupId = 1L;

		CustomUserDetails userDetails = createMockUserDetails();

		when(groupService.isMemberOfGroup(eq(groupId), eq(userDetails.getUser().getId())))
			.thenReturn(true);

		mockMvc.perform(get("/community/groupMember/{groupId}/membership", groupId)
				.with(user(userDetails)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").value(true))
			.andDo(print());

		verify(groupService).isMemberOfGroup(eq(groupId), eq(userDetails.getUser().getId()));
	}

	@Test
	@WithMockUser
	void isMemberOfGroup_멤버가_아닌_경우() throws Exception {
		Long groupId = 1L;

		// 모의 사용자 설정 (테스트용 사용자)
		CustomUserDetails userDetails = createMockUserDetails();

		when(groupService.isMemberOfGroup(eq(groupId), eq(userDetails.getUser().getId())))
			.thenReturn(false);

		mockMvc.perform(get("/community/groupMember/{groupId}/membership", groupId)
				.with(user(userDetails)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").value(false))
			.andDo(print());

		verify(groupService).isMemberOfGroup(eq(groupId), eq(userDetails.getUser().getId()));
	}

	private CustomUserDetails createMockUserDetails() {
		User mockUser = User.builder()
			.id("test-user-id")
			.tel("010-1111-1111")
			.build();

		return new CustomUserDetails(mockUser);
	}
}
