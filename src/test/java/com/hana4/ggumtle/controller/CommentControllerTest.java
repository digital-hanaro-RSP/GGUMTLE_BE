package com.hana4.ggumtle.controller;

import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.hana4.ggumtle.dto.comment.CommentLikeResponseDto;
import com.hana4.ggumtle.dto.comment.CommentRequestDto;
import com.hana4.ggumtle.dto.comment.CommentResponseDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.CommentService;

@WebMvcTest(controllers = CommentController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	})
@WithMockCustomUser
@Import(TestSecurityConfig.class)
class CommentControllerTest {

	@MockitoBean
	CommentService commentService;

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
	void addComment() throws Exception {
		// given
		String content = "content";
		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		CommentRequestDto.CommentWrite commentWrite = new CommentRequestDto.CommentWrite(content);
		CommentResponseDto.CommentInfo commentInfo = new CommentResponseDto.CommentInfo(1L, 1L, content,
			UserResponseDto.BriefInfo.from(customUserDetails.getUser()), false, true, 0);
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(commentService.saveComment(eq(1L), eq(commentWrite), eq(customUserDetails.getUser()))).willReturn(
			commentInfo);

		// when
		String reqBody = objectMapper.writeValueAsString(commentWrite);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/community/post/{postId}/comment", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(commentInfo.getId()))
			.andExpect(jsonPath("$.data.content").value(commentInfo.getContent()))
			.andExpect(jsonPath("$.data.userBriefInfo.name").value(commentInfo.getUserBriefInfo().getName()))
			.andExpect(jsonPath("$.data.postId").value(commentInfo.getPostId()))
			.andExpect(jsonPath("$.data.mine").value(commentInfo.isMine()))
			.andDo(print());

		verify(commentService).saveComment(1L, commentWrite, customUserDetails.getUser());
	}

	@Test
	void getComments() throws Exception {
		// given
		Long groupId = 1L;
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		String content = "content";

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<CommentResponseDto.CommentInfo> commentInfos = new ArrayList<>();

		CommentResponseDto.CommentInfo commentInfo = new CommentResponseDto.CommentInfo(1L, 1L, content,
			UserResponseDto.BriefInfo.from(customUserDetails.getUser()), false, true, 0);
		for (int i = 0; i < 3; i++)
			commentInfos.add(commentInfo);
		Page<CommentResponseDto.CommentInfo> pages = new PageImpl<>(commentInfos, pageable, commentInfos.size());

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(commentService.getCommentsByPage(eq(groupId), eq(pageable), eq(customUserDetails.getUser()))).willReturn(
			pages);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/post/{postId}/comments", groupId, postId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.content[0].content").value(commentInfos.get(0).getContent()))
			.andExpect(jsonPath("$.data.content[1].content").value(commentInfos.get(1).getContent()))
			.andExpect(jsonPath("$.data.content[2].content").value(commentInfos.get(2).getContent()))
			.andDo(print());

		verify(commentService).getCommentsByPage(groupId, pageable, customUserDetails.getUser());
	}

	@Test
	void deleteComment() throws Exception {
		// given
		Long commentId = 1L;

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.delete("/community/comment/{commentId}", commentId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andDo(print());

		verify(commentService).deleteComment(commentId);
	}

	@Test
	void likeComment() throws Exception {
		// given
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		CommentRequestDto.CommentWrite write = CommentRequestDto.CommentWrite.builder().content("content").build();

		Post post = new Post();
		post.setId(1L);

		CommentLikeResponseDto.CommentLikeInfo commentLikeInfo = new CommentLikeResponseDto.CommentLikeInfo(1L,
			customUserDetails.getUser().getId(), 1L, LocalDateTime.now());
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(commentService.addLike(eq(1L), eq(customUserDetails.getUser()))).willReturn(commentLikeInfo);

		// when
		String reqBody = objectMapper.writeValueAsString(write);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/community/comment/{commentId}/like", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(1L))
			.andExpect(jsonPath("$.data.commentId").value(1L))
			.andDo(print());

		verify(commentService).addLike(1L, customUserDetails.getUser());
	}

	@Test
	void dislikeComment() throws Exception {
		// given
		Long commentId = 1L;

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.delete("/community/comment/{commentId}/dislike", commentId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andDo(print());

		verify(commentService).removeLike(commentId, customUserDetails.getUser());
	}
}