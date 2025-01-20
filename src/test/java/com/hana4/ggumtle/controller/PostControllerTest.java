package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import com.hana4.ggumtle.dto.user.UserResponseDto;
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

	@Test
	void getPost() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		PostResponseDto.PostDetail postDetail = PostResponseDto.PostDetail.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.userBriefInfo(UserResponseDto.BriefInfo.from(customUserDetails.getUser()))
			.likeCount(1)
			.commentCount(1)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPost(eq(groupId), eq(postId), eq(customUserDetails.getUser()))).willReturn(postDetail);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/group/{groupId}/post/{postId}", groupId, postId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(postDetail.getUserId()))
			.andExpect(jsonPath("$.data.groupId").value(postDetail.getGroupId()))
			.andExpect(jsonPath("$.data.imageUrls").value(postDetail.getImageUrls()))
			.andExpect(jsonPath("$.data.content").value(postDetail.getContent()))
			.andExpect(jsonPath("$.data.postType").value(postDetail.getPostType().name()))
			.andDo(print());

		verify(postService).getPost(groupId, postId, customUserDetails.getUser());
	}

	@Test
	void getPostsByPage() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;
		int page = 0;

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<PostResponseDto.PostInfo> postInfos = new ArrayList<>();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		for (int i = 0; i < 10; i++)
			postInfos.add(postInfo);

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPostsByPage(eq(groupId), eq(page), eq(customUserDetails.getUser()))).willReturn(postInfos);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/group/{groupId}/post", groupId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data[0].userId").value(postInfos.get(0).getUserId()))
			.andExpect(jsonPath("$.data[1].userId").value(postInfos.get(1).getUserId()))
			.andExpect(jsonPath("$.data[2].userId").value(postInfos.get(2).getUserId()))
			.andExpect(jsonPath("$.data[3].userId").value(postInfos.get(3).getUserId()))
			.andExpect(jsonPath("$.data[4].userId").value(postInfos.get(4).getUserId()))
			.andExpect(jsonPath("$.data[5].userId").value(postInfos.get(5).getUserId()))
			.andExpect(jsonPath("$.data[6].userId").value(postInfos.get(6).getUserId()))
			.andExpect(jsonPath("$.data[7].userId").value(postInfos.get(7).getUserId()))
			.andExpect(jsonPath("$.data[8].userId").value(postInfos.get(8).getUserId()))
			.andExpect(jsonPath("$.data[9].userId").value(postInfos.get(9).getUserId()))
			.andDo(print());

		verify(postService).getPostsByPage(groupId, 0, customUserDetails.getUser());
	}

	@Test
	void updatePost() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;

		String imageUrls = "imageUrl";
		String content = "글 내용";
		String snapShot = "{\"bucketId\":[3,2,1],\"portfolio\":false}";

		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.imageUrls(imageUrls)
			.content(content)
			.snapShot(snapShot)
			.build();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot(snapShot)
			.imageUrls(imageUrls)
			.content(content)
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.updatePost(eq(1L), eq(1L), eq(write), eq(customUserDetails.getUser()))).willReturn(postInfo);

		// when
		String reqBody = objectMapper.writeValueAsString(write);

		// then
		mockMvc.perform(MockMvcRequestBuilders.patch("/community/group/{groupId}/post/{postId}", groupId, postId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(postInfo.getUserId()))
			.andExpect(jsonPath("$.data.groupId").value(postInfo.getGroupId()))
			.andExpect(jsonPath("$.data.imageUrls").value(postInfo.getImageUrls()))
			.andExpect(jsonPath("$.data.content").value(postInfo.getContent()))
			.andExpect(jsonPath("$.data.postType").value(postInfo.getPostType().name()))
			.andDo(print());

		verify(postService).updatePost(groupId, postId, write, customUserDetails.getUser());
	}

	@Test
	void deletePost() throws Exception {
		// given
		Long groupId = 1L;
		Long postId = 1L;

		String imageUrls = "imageUrl";
		String content = "글 내용";
		String snapShot = "{\"bucketId\":[3,2,1],\"portfolio\":false}";

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.delete("/community/group/{groupId}/post/{postId}", groupId, postId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andDo(print());

		verify(postService).deletePost(groupId, postId, customUserDetails.getUser());
	}
}
