package com.hana4.ggumtle.controller;

import static org.hamcrest.collection.IsCollectionWithSize.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.dto.image.ImageRequestDto;
import com.hana4.ggumtle.security.filter.JwtAuthFilter;
import com.hana4.ggumtle.security.provider.JwtProvider;
import com.hana4.ggumtle.service.CustomUserDetailsService;
import com.hana4.ggumtle.service.PresignedUrlService;

@WebMvcTest(ImageUploadController.class)
@WithMockCustomUser
@Import(TestSecurityConfig.class)
class ImageUploadControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	private PresignedUrlService presignedUrlService;

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
	void testGenerateMultiplePresignedUrls() throws Exception {
		List<ImageRequestDto.Upload.Image> images = Arrays.asList(
			new ImageRequestDto.Upload.Image("image1.png", 1024L),
			new ImageRequestDto.Upload.Image("image2.png", 2048L)
		);
		ImageRequestDto.Upload imageUploadRequest = new ImageRequestDto.Upload(images);

		List<String> presignedUrls = Arrays.asList(
			"https://presignedurl.com/1",
			"https://presignedurl.com/2"
		);

		String mockToken = "mock.jwt.token";
		when(jwtProvider.validateToken(mockToken)).thenReturn(true);
		when(jwtProvider.getUsernameFromToken(mockToken)).thenReturn("testuser");

		// UserDetails 모의 처리
		UserDetails userDetails = User.withUsername("testuser")
			.password("password")
			.roles("USER")
			.build();
		when(customUserDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

		when(presignedUrlService.generateMultiplePresignedUrls(any(ImageRequestDto.Upload.class)))
			.thenReturn(presignedUrls);

		mockMvc.perform(post("/imageUpload/multiple")
				.header("Authorization", "Bearer " + mockToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(imageUploadRequest))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data", hasSize(2)))
			.andExpect(jsonPath("$.data[0]").value("https://presignedurl.com/1"))
			.andExpect(jsonPath("$.data[1]").value("https://presignedurl.com/2"))
			.andDo(print());

		verify(presignedUrlService).generateMultiplePresignedUrls(any(ImageRequestDto.Upload.class));
	}
}

