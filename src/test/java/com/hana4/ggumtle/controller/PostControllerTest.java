package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.model.entity.post.PostType;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.PostService;

@WebMvcTest(controllers = PostController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	})
@WithMockCustomUser
@Import(TestSecurityConfig.class)
class PostControllerTest {

	@MockitoBean
	PostService postService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity()) // Spring Security 통합
			.build();
	}

	@Test
	void writePost() throws Exception {

		// given
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		PostRequestDto.Write write = new PostRequestDto.Write(imageUrls, content, snapShot, PostType.POST);

		PostResponseDto.PostInfo post = new PostResponseDto.PostInfo(
			1L,
			"1", // userId
			1L, // groupId
			null, // snapShot
			imageUrls, // imageUrls
			content, // content
			PostType.POST, // postType
			false
		);

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.save(eq(1L), eq(write), eq(customUserDetails.getUser()))).willReturn(post);

		// when
		String reqBody = objectMapper.writeValueAsString(write);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/community/group/{groupId}/post", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(post.getUserId()))
			.andExpect(jsonPath("$.data.groupId").value(post.getGroupId()))
			.andExpect(jsonPath("$.data.imageUrls").value(post.getImageUrls()))
			.andExpect(jsonPath("$.data.content").value(post.getContent()))
			.andExpect(jsonPath("$.data.postType").value(post.getPostType().name()))
			.andDo(print());

		verify(postService).save(1L, write, customUserDetails.getUser());
	}
}
