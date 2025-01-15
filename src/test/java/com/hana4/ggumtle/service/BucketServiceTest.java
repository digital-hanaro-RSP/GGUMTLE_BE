package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.hana4.ggumtle.dto.bucketlist.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketlist.BucketResponseDto;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.repository.BucketRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BucketServiceTest {

	@InjectMocks
	private BucketService bucketService;

	@Mock
	private BucketRepository bucketRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createBucket_shouldReturnCreatedBucketInfo() {
		// given
		BucketRequestDto requestDto = BucketRequestDto.builder()
			.title("New Bucket")
			.isRecommended(false)
			.goalAmount(BigDecimal.valueOf(1000))
			.build();

		Bucket bucketEntity = Bucket.builder()
			.title("New Bucket")
			.isRecommended(false)
			.goalAmount(BigDecimal.valueOf(1000))
			.build();

		when(bucketRepository.save(any(Bucket.class))).thenReturn(bucketEntity);

		// when
		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto);

		// then
		assertThat(result.getTitle()).isEqualTo("New Bucket");
		assertThat(result.getGoalAmount()).isEqualTo(BigDecimal.valueOf(1000));
		verify(bucketRepository, times(1)).save(any(Bucket.class));
	}
	@InjectMocks
	private BucketService bucketService;

	@Test
	void createBucket_shouldThrowExceptionWhenRecommendedButNoOriginId() {
		// given
		BucketRequestDto requestDto = BucketRequestDto.builder()
			.isRecommended(true)
			.originId(null)
			.build();
	void getBucket_성공() {
		when(bucketRepository.findById(1L)).thenReturn(Optional.of(new Bucket()));

		// when & then
		assertThatThrownBy(() -> bucketService.createBucket(requestDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("추천 플로우에서는 originId가 필요합니다.");
		verify(bucketRepository, never()).save(any(Bucket.class));
		assertThat(bucketService.getBucket(1L)).isNotNull();
	}

	@Test
	void updateBucket_shouldReturnUpdatedBucketInfo() {
		// given
		Long bucketId = 1L;

		Bucket existingBucket = Bucket.builder()
			.id(bucketId)
			.title("Old Bucket")
			.goalAmount(BigDecimal.valueOf(500))
			.build();

		BucketRequestDto requestDto = BucketRequestDto.builder()
			.title("Updated Bucket")
			.goalAmount(BigDecimal.valueOf(1500))
			.build();

		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(existingBucket));
	void getBucket_실패() {
		// Given
		Long postId = 1L;
		when(bucketRepository.findById(postId)).thenReturn(Optional.empty());

		// when
		BucketResponseDto.BucketInfo result = bucketService.updateBucket(bucketId, requestDto);
		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			bucketService.getBucket(postId);
		});

		// then
		assertThat(result.getTitle()).isEqualTo("Updated Bucket");
		assertThat(result.getGoalAmount()).isEqualTo(BigDecimal.valueOf(1500));
		verify(bucketRepository, times(1)).findById(bucketId);
		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 Bucket이 존재하지 않습니다.", exception.getMessage());
		verify(bucketRepository).findById(postId);
	}

	@Test
	void updateBucket_shouldThrowExceptionWhenBucketNotFound() {
		// given
		Long bucketId = 1L;
		BucketRequestDto requestDto = BucketRequestDto.builder()
			.title("Updated Bucket")
			.goalAmount(BigDecimal.valueOf(1500))
			.build();

		when(bucketRepository.findById(bucketId)).thenReturn(null);

		// when & then
		assertThatThrownBy(() -> bucketService.updateBucket(bucketId, requestDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("버킷을 찾을 수 없습니다.");
		verify(bucketRepository, times(1)).findById(bucketId);
	}
}

}