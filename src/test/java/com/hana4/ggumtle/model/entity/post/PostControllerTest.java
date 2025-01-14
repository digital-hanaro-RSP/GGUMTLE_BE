package com.hana4.ggumtle.model.entity.post;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.security.provider.JwtProvider;

@WebMvcTest(PostController.class)
// @WithMockUser(username = "kkkk")
@Import(TestSecurityConfig.class)
class PostControllerTest {
	@MockitoBean
	private PostService postService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	JwtProvider jwtProvider;

	@Test
	void writePost() throws Exception {
		// given
		String imageUrls = "imageUrl";
		String content = "content";
		PostRequestDto.Write write = new PostRequestDto.Write(imageUrls, content, PostType.POST);
		PostResponseDto.PostInfo postInfo = new PostResponseDto.PostInfo("1", 1L, 1L, null, imageUrls, content,
			PostType.POST);

		// when
		when(postService.save("1", 1L, write)).thenReturn(postInfo);

		// then
		String reqBody = objectMapper.writeValueAsString(write);

		mockMvc.perform(MockMvcRequestBuilders.post("/community/group/{groupId}/post/{userId}", 1L, "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(1))
			.andExpect(jsonPath("$.data.groupId").value(1))
			.andExpect(jsonPath("$.data.bucketId").value(1))
			.andExpect(jsonPath("$.data.snapShot").doesNotExist())
			.andExpect(jsonPath("$.data.imageUrls").value(imageUrls))
			.andExpect(jsonPath("$.data.content").value(content))
			.andExpect(jsonPath("$.data.postType").value("POST"))
			.andDo(print());
	}
}