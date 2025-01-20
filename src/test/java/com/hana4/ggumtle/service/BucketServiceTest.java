package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.repository.BucketRepository;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {
	@Mock
	private BucketRepository bucketRepository;

	@InjectMocks
	private BucketService bucketService;

	@Test
	void getBucket_성공() {
		when(bucketRepository.findById(1L)).thenReturn(Optional.of(new Bucket()));

		assertThat(bucketService.getBucket(1L)).isNotNull();
	}

	@Test
	void getBucket_실패() {
		// Given
		Long postId = 1L;
		when(bucketRepository.findById(postId)).thenReturn(Optional.empty());

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			bucketService.getBucket(postId);
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 Bucket이 존재하지 않습니다.", exception.getMessage());
		verify(bucketRepository).findById(postId);
	}
}
