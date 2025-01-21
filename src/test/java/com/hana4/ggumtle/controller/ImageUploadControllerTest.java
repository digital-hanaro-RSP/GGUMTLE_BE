package com.hana4.ggumtle.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
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
	void testGeneratePresignedUrl() throws Exception {
		String objectKey = "test/image.png";
		String presignedUrl = "https://presignedurl.com";

		when(presignedUrlService.generatePresignedUrl(objectKey, 15)).thenReturn(new URL(presignedUrl));

		mockMvc.perform(get("/imageUpload")
				.param("objectKey", objectKey)
				.param("expirationMinutes", "15")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").value(presignedUrl))
			.andDo(print());
	}

	@Test
	@WithMockUser
	void testGeneratePresignedUrl_error() throws Exception {
		String objectKey = "test/image.png";

		when(presignedUrlService.generatePresignedUrl(objectKey, 15))
			.thenThrow(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get("/imageUpload")
				.param("objectKey", objectKey)
				.param("expirationMinutes", "15")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value()))
			.andExpect(jsonPath("$.message").value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
			.andDo(print());
	}
}
