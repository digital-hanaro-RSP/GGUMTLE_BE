package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
public class PresignedUrlServiceTest {
	private PresignedUrlService presignedUrlService;

	@Mock
	private S3Presigner mockS3Presigner;

	@BeforeEach
	void setUp() {
		presignedUrlService = new PresignedUrlService("test-bucket", "ap-northeast-2");
		ReflectionTestUtils.setField(presignedUrlService, "s3Presigner", mockS3Presigner);
	}

	@Test
	void generatePresignedUrl_ShouldReturnValidUrl() throws Exception {
		// Arrange
		String objectKey = "test/image.png";
		int expirationMinutes = 15;
		URL mockUrl = new URL("https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + objectKey
			+ "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250119T025413Z&X-Amz-SignedHeaders=host"
			+ "&X-Amz-Expires=900&X-Amz-Credential=test-credential&X-Amz-Signature=test-signature");

		PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
		when(mockPresignedRequest.url()).thenReturn(mockUrl);
		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

		// Act
		URL presignedUrl = presignedUrlService.generatePresignedUrl(objectKey, expirationMinutes);

		// Assert
		assertThat(presignedUrl).isNotNull();
		String urlString = presignedUrl.toString();
		assertThat(urlString)
			.contains("https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + objectKey)
			.contains("X-Amz-Algorithm=AWS4-HMAC-SHA256")
			.matches(".*X-Amz-Date=\\d{8}T\\d{6}Z.*")
			.contains("X-Amz-SignedHeaders=host")
			.matches(".*X-Amz-Expires=900.*")
			.contains("X-Amz-Credential=")
			.contains("X-Amz-Signature=");

		// Verify
		verify(mockS3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
	}
}