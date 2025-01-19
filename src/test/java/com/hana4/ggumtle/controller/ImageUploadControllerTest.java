package com.hana4.ggumtle.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hana4.ggumtle.service.PresignedUrlService;

@ExtendWith(MockitoExtension.class)
class ImageUploadControllerTest {

	private MockMvc mockMvc;

	@Mock
	private PresignedUrlService presignedUrlService;

	@InjectMocks
	private ImageUploadController imageUploadController;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(imageUploadController).build();
	}

	@Test
	void testGeneratePresignedUrl() throws Exception {
		String objectKey = "test/image.png";
		String presignedUrl = "https://presignedurl.com";

		when(presignedUrlService.generatePresignedUrl(objectKey, 15)).thenReturn(new URL(presignedUrl));

		mockMvc.perform(get("/imageUpload")
				.param("objectKey", objectKey)
				.param("expirationMinutes", "15"))
			.andExpect(status().isOk())
			.andExpect(content().string(presignedUrl));
	}

	@Test
	void testGeneratePresignedUrl_error() throws Exception {
		String objectKey = "test/image.png";
		when(presignedUrlService.generatePresignedUrl(objectKey, 15)).thenThrow(
			new RuntimeException("Error generating presigned URL"));

		mockMvc.perform(get("/imageUpload")
				.param("objectKey", objectKey)
				.param("expirationMinutes", "15"))
			.andExpect(status().isInternalServerError())
			.andExpect(content().string("Error generating presigned URL: Error generating presigned URL"));
	}
}