package com.hana4.ggumtle.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
public class PresignedUrlServiceTest {
	private PresignedUrlService presignedUrlService;

	@Mock
	private S3Presigner s3Presigner;

	@Mock
	private S3Presigner mockS3Presigner;

	@BeforeEach
	void setUp() {
		presignedUrlService = new PresignedUrlService("test-bucket", "ap-northeast-2");
		ReflectionTestUtils.setField(presignedUrlService, "s3Presigner", mockS3Presigner);
	}

	@Test
	void generatePresignedUrl_url반환성공() throws Exception {
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
		assertThat(presignedUrl).isEqualTo(mockUrl);  // 직접적으로 반환된 URL 비교
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
		verify(mockPresignedRequest, times(1)).url();  // url() 메소드 호출 확인
	}

	@Test
	void testGeneratePresignedUrl_내부서버오류() {
		// Arrange
		String objectKey = "test/image.png";
		int expirationMinutes = 15;

		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
			.thenThrow(new RuntimeException("S3 error"));

		// Act & Assert
		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generatePresignedUrl(objectKey, expirationMinutes);
		});

		// Verify
		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
		assertEquals("내부 서버 오류", exception.getMessage()); // ErrorCode.INTERNAL_SERVER_ERROR의 메시지 확인
		verify(mockS3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
	}
}
