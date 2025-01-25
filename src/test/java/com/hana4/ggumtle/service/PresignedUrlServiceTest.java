package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.hana4.ggumtle.dto.image.ImageRequestDto;
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
		String objectKey = "test/image.png";
		int expirationMinutes = 15;
		URL mockUrl = new URL("https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + objectKey
			+ "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250119T025413Z&X-Amz-SignedHeaders=host"
			+ "&X-Amz-Expires=900&X-Amz-Credential=test-credential&X-Amz-Signature=test-signature");

		PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
		when(mockPresignedRequest.url()).thenReturn(mockUrl);
		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

		URL presignedUrl = presignedUrlService.generatePresignedUrl(objectKey, expirationMinutes);

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

		verify(mockS3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
		verify(mockPresignedRequest, times(1)).url();  // url() 메소드 호출 확인
	}

	@Test
	void testGeneratePresignedUrl_내부서버오류() {
		String objectKey = "test/image.png";
		int expirationMinutes = 15;

		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
			.thenThrow(new RuntimeException("S3 error"));

		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generatePresignedUrl(objectKey, expirationMinutes);
		});

		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
		assertEquals("내부 서버 오류", exception.getMessage()); // ErrorCode.INTERNAL_SERVER_ERROR의 메시지 확인
		verify(mockS3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
	}

	@Test
	void generateMultiplePresignedUrls_성공케이스() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Arrays.asList(ImageRequestDto.Upload.Image.builder().name("test1.png").size(500_000L).build(),
				ImageRequestDto.Upload.Image.builder().name("test2.jpg").size(500_000L).build()))
			.build();
		URL mockUrl1 = createMockUrl("test1.png");
		URL mockUrl2 = createMockUrl("test2.jpg");
		PresignedPutObjectRequest mockPresignedRequest1 = mock(PresignedPutObjectRequest.class);
		PresignedPutObjectRequest mockPresignedRequest2 = mock(PresignedPutObjectRequest.class);
		when(mockPresignedRequest1.url()).thenReturn(mockUrl1);
		when(mockPresignedRequest2.url()).thenReturn(
			mockUrl2);

		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mockPresignedRequest1)
			.thenReturn(
				mockPresignedRequest2);
		List<String> presignedUrls = presignedUrlService.generateMultiplePresignedUrls(
			imageUploadRequest);
		assertThat(presignedUrls).hasSize(2);
		assertThat(presignedUrls.get(0)).contains("test1.png");
		assertThat(presignedUrls.get(1)).contains(
			"test2.jpg");
		verify(mockS3Presigner, times(2)).presignPutObject(any(PutObjectPresignRequest.class));
	}

	@Test
	void generateMultiplePresignedUrls_파일크기초과() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Arrays.asList(
				ImageRequestDto.Upload.Image.builder()
					.name("large-image.png")
					.size(2 * 1024 * 1024L) // 2MB
					.build()))
			.build();
		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generateMultiplePresignedUrls(imageUploadRequest);
		});
		assertEquals(ErrorCode.FILE_SIZE_EXCEEDED, exception.getErrorCode());
	}

	@Test
	void generateMultiplePresignedUrls_빈_이미지_리스트() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Collections.emptyList())
			.build();

		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generateMultiplePresignedUrls(imageUploadRequest);
		});
		assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());
	}

	@Test
	void generateMultiplePresignedUrls_S3_내부서버오류() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Arrays.asList(ImageRequestDto.Upload.Image.builder().name("test.png").size(500_000L).build()))
			.build();       // S3Presigner 메서드에서 예외 발생

		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenThrow(new RuntimeException(
			"S3 Presigner Error"));

		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generateMultiplePresignedUrls(imageUploadRequest);
		});

		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
	}

	private URL createMockUrl(String objectKey) {
		try {
			return new URL("https://test-bucket.s3.ap-northeast-2.amazonaws.com/" + objectKey
				+ "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250119T025413Z&X-Amz-SignedHeaders=host"
				+ "&X-Amz-Expires=900&X-Amz-Credential=test-credential&X-Amz-Signature=test-signature");
		} catch (MalformedURLException e) {
			throw new RuntimeException("URL 생성 실패", e);
		}
	}

	@Test
	void generateMultiplePresignedUrls_Null_이미지_리스트() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(null)
			.build();

		assertThatThrownBy(() -> presignedUrlService.generateMultiplePresignedUrls(imageUploadRequest)).isInstanceOf(
			NullPointerException.class);
	}

	@Test
	void generateMultiplePresignedUrls_최대_파일크기_경계값_테스트() {
		long maxSize = 1 * 1024 * 1024; // 1MB
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Arrays.asList(ImageRequestDto.Upload.Image.builder()
				.name("boundary-image.png").size(maxSize)  // 정확히 1MB
				.build()))
			.build();

		URL mockUrl = createMockUrl("boundary-image.png");
		PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
		when(mockPresignedRequest.url()).thenReturn(mockUrl);
		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(
			mockPresignedRequest);

		List<String> presignedUrls = presignedUrlService.generateMultiplePresignedUrls(
			imageUploadRequest);

		assertThat(presignedUrls).hasSize(1);
	}

	@Test
	void generateMultiplePresignedUrls_다중_파일_크기_제한_테스트() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Arrays.asList(
				ImageRequestDto.Upload.Image.builder()
					.name("image1.png")
					.size(500_000L)
					.build(),
				ImageRequestDto.Upload.Image.builder()
					.name("large-image.jpg")
					.size(2 * 1024 * 1024L) // 2MB
					.build()))
			.build();

		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generateMultiplePresignedUrls(imageUploadRequest);
		});
		assertEquals(ErrorCode.FILE_SIZE_EXCEEDED, exception.getErrorCode());
	}

	@Test
	void generateMultiplePresignedUrls_S3_연결_실패_상세_예외() {
		ImageRequestDto.Upload imageUploadRequest = ImageRequestDto.Upload.builder()
			.images(Arrays.asList(ImageRequestDto.Upload.Image.builder().name("test.png").size(500_000L).build()))
			.build();

		when(mockS3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenThrow(new RuntimeException(
			"Detailed S3 Connection Error"));

		CustomException exception = assertThrows(CustomException.class, () -> {
			presignedUrlService.generateMultiplePresignedUrls(imageUploadRequest);
		});
		assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
	}
}


