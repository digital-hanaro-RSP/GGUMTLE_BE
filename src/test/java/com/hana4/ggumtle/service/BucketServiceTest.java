// // package com.hana4.ggumtle.service;
// //
// // import static org.assertj.core.api.Assertions.*;
// // import static org.junit.jupiter.api.Assertions.*;
// // import static org.mockito.Mockito.*;
// //
// // import java.math.BigDecimal;
// // import java.util.Optional;
// //
// // import org.junit.jupiter.api.BeforeEach;
// // import org.junit.jupiter.api.Test;
// // import org.junit.jupiter.api.extension.ExtendWith;
// // import org.mockito.InjectMocks;
// // import org.mockito.Mock;
// // import org.mockito.MockitoAnnotations;
// // import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// // import org.springframework.boot.test.context.SpringBootTest;
// //
// // import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
// // import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
// // import com.hana4.ggumtle.global.error.CustomException;
// // import com.hana4.ggumtle.global.error.ErrorCode;
// // import com.hana4.ggumtle.model.entity.bucket.Bucket;
// // import com.hana4.ggumtle.repository.BucketRepository;
// //
// // @SpringBootTest
// // @AutoConfigureMockMvc
// // @ExtendWith(MockitoExtension.class)
// // class BucketServiceTest {
// //
// // 	@InjectMocks
// // 	private BucketService bucketService;
// //
// // 	@Mock
// // 	private BucketRepository bucketRepository;
// //
// // 	@BeforeEach
// // 	void setUp() {
// // 		MockitoAnnotations.openMocks(this);
// // 	}
// //
// // 	@Test
// // 	void createBucket_shouldReturnCreatedBucketInfo() {
// // 		// given
// // 		BucketRequestDto requestDto = BucketRequestDto.builder()
// // 			.title("New Bucket")
// // 			.isRecommended(false)
// // 			.goalAmount(BigDecimal.valueOf(1000))
// // 			.build();
// //
// // 		Bucket bucketEntity = Bucket.builder()
// // 			.title("New Bucket")
// // 			.isRecommended(false)
// // 			.goalAmount(BigDecimal.valueOf(1000))
// // 			.build();
// //
// // 		when(bucketRepository.save(any(Bucket.class))).thenReturn(bucketEntity);
// //
// // 		// when
// // 		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto);
// //
// // 		// then
// // 		assertThat(result.getTitle()).isEqualTo("New Bucket");
// // 		assertThat(result.getGoalAmount()).isEqualTo(BigDecimal.valueOf(1000));
// // 		verify(bucketRepository, times(1)).save(any(Bucket.class));
// // 	}
// // 	@InjectMocks
// // 	private BucketService bucketService;
// //
// // 	@Test
// // 	void createBucket_shouldThrowExceptionWhenRecommendedButNoOriginId() {
// // 		// given
// // 		BucketRequestDto requestDto = BucketRequestDto.builder()
// // 			.isRecommended(true)
// // 			.originId(null)
// // 			.build();
// // 	void getBucket_성공() {
// // 		when(bucketRepository.findById(1L)).thenReturn(Optional.of(new Bucket()));
// //
// // 		// when & then
// // 		assertThatThrownBy(() -> bucketService.createBucket(requestDto))
// // 			.isInstanceOf(IllegalArgumentException.class)
// // 			.hasMessage("추천 플로우에서는 originId가 필요합니다.");
// // 		verify(bucketRepository, never()).save(any(Bucket.class));
// // 		assertThat(bucketService.getBucket(1L)).isNotNull();
// // 	}
// //
// // 	@Test
// // 	void updateBucket_shouldReturnUpdatedBucketInfo() {
// // 		// given
// // 		Long bucketId = 1L;
// //
// // 		Bucket existingBucket = Bucket.builder()
// // 			.id(bucketId)
// // 			.title("Old Bucket")
// // 			.goalAmount(BigDecimal.valueOf(500))
// // 			.build();
// //
// // 		BucketRequestDto requestDto = BucketRequestDto.builder()
// // 			.title("Updated Bucket")
// // 			.goalAmount(BigDecimal.valueOf(1500))
// // 			.build();
// //
// // 		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(existingBucket));
// // 	void getBucket_실패() {
// // 		// Given
// // 		Long postId = 1L;
// // 		when(bucketRepository.findById(postId)).thenReturn(Optional.empty());
// //
// // 		// when
// // 		BucketResponseDto.BucketInfo result = bucketService.updateBucket(bucketId, requestDto);
// // 		// When & Then
// // 		CustomException exception = assertThrows(CustomException.class, () -> {
// // 			bucketService.getBucket(postId);
// // 		});
// //
// // 		// then
// // 		assertThat(result.getTitle()).isEqualTo("Updated Bucket");
// // 		assertThat(result.getGoalAmount()).isEqualTo(BigDecimal.valueOf(1500));
// // 		verify(bucketRepository, times(1)).findById(bucketId);
// // 		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
// // 		assertEquals("해당 Bucket이 존재하지 않습니다.", exception.getMessage());
// // 		verify(bucketRepository).findById(postId);
// // 	}
// //
// // 	@Test
// // 	void updateBucket_shouldThrowExceptionWhenBucketNotFound() {
// // 		// given
// // 		Long bucketId = 1L;
// // 		BucketRequestDto requestDto = BucketRequestDto.builder()
// // 			.title("Updated Bucket")
// // 			.goalAmount(BigDecimal.valueOf(1500))
// // 			.build();
// //
// // 		when(bucketRepository.findById(bucketId)).thenReturn(null);
// //
// // 		// when & then
// // 		assertThatThrownBy(() -> bucketService.updateBucket(bucketId, requestDto))
// // 			.isInstanceOf(IllegalArgumentException.class)
// // 			.hasMessage("버킷을 찾을 수 없습니다.");
// // 		verify(bucketRepository, times(1)).findById(bucketId);
// // 	}
// // }
// //
// // }
// package com.hana4.ggumtle.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
// import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
// import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
// import com.hana4.ggumtle.model.entity.bucket.Bucket;
// import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
// import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
// import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
// import com.hana4.ggumtle.model.entity.user.User;
// import com.hana4.ggumtle.repository.BucketRepository;
//
// @ExtendWith(MockitoExtension.class)
// class BucketServiceTest {
//
// 	@Mock
// 	private BucketRepository bucketRepository;
//
// 	@InjectMocks
// 	private BucketService bucketService;
//
// 	@Test
// 	void createBucket_ShouldCreateNewBucket() {
// 		// given
// 		User mockUser = new User();
// 		BucketRequestDto.Create requestDto = new BucketRequestDto.Create(
// 			"Bucket 1",
// 			BucketTagType.BE,
// 			LocalDateTime.now(),
// 			BucketHowTo.MONEY,
// 			true,
// 			BigDecimal.valueOf(5000),
// 			true,
// 			BigDecimal.valueOf(1000),
// 			2000L,
// 			"0 0 12 * * ?",
// 			BigDecimal.valueOf(1000),
// 			BucketStatus.DOING,
// 			"memo",
// 			true,
// 			10L
// 		);
// 		Bucket savedBucket = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.save(any(Bucket.class))).thenReturn(savedBucket);
//
// 		// when
// 		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, mockUser);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.getTitle()).isEqualTo("Bucket 1");
// 		verify(bucketRepository, times(1)).save(any(Bucket.class));
// 	}
//
// 	@Test
// 	void createBucket_whenIsRecommendedAndOriginIdIsNull_thenThrowsException() {
// 		// Given
// 		BucketRequestDto.Create requestDto = BucketRequestDto.Create.builder()
// 			.isRecommended(true)
// 			.originId(null) // originId가 null
// 			.build();
// 		User user = new User(); // 테스트용 User 객체
//
// 		// When & Then
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.createBucket(requestDto, user));
// 		assertEquals("추천 플로우에서는 originId가 필요합니다.", exception.getMessage());
// 	}
//
// 	@Test
// 	void createBucket_whenIsRecommendedAndOriginIdIsNotNull_thenCreatesBucket() {
// 		// Given
// 		User user = new User(); // 테스트용 User 객체
// 		user.setId("1"); // User 객체에 ID 설정
//
// 		Bucket bucket = Bucket.builder()
// 			.id(1L)
// 			.title("Test Bucket")
// 			.user(user) // Bucket에 User 설정
// 			.build();
//
// 		BucketRequestDto.Create requestDto = BucketRequestDto.Create.builder()
// 			.isRecommended(true)
// 			.originId(123L) // 유효한 originId
// 			.build();
//
// 		// Mock 설정: save 호출 시 반환할 Bucket 객체
// 		when(bucketRepository.save(any(Bucket.class))).thenReturn(bucket);
//
// 		// When
// 		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, user);
//
// 		// Then
// 		assertNotNull(result);
// 		assertEquals(bucket.getId(), result.getId());
// 		assertEquals(bucket.getTitle(), result.getTitle());
// 		assertEquals(user.getId(), result.getUserId()); // User ID 확인
// 	}
//
// 	@Test
// 	void createBucket_whenIsNotRecommended_thenCreatesBucket() {
// 		// Given
// 		User user = new User(); // 테스트용 User 객체 생성
// 		user.setId("1"); // ID 설정
//
// 		Bucket bucket = Bucket.builder()
// 			.id(1L)
// 			.title("Non-recommended Bucket")
// 			.user(user) // User 설정
// 			.build();
//
// 		BucketRequestDto.Create requestDto = BucketRequestDto.Create.builder()
// 			.isRecommended(false) // 추천 플로우 아님
// 			.build();
//
// 		// `bucketRepository.save()`의 반환값 설정
// 		when(bucketRepository.save(any(Bucket.class))).thenReturn(bucket);
//
// 		// When
// 		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, user);
//
// 		// Then
// 		assertNotNull(result);
// 		assertEquals(bucket.getId(), result.getId());
// 		assertEquals(bucket.getTitle(), result.getTitle());
// 		assertEquals(user.getId(), result.getUserId());
// 	}
//
// 	@Test
// 	void updateBucket_ShouldUpdateBucket() {
// 		// given
// 		User mockUser = new User();
//
// 		BucketRequestDto.Create requestDto = new BucketRequestDto.Create(
// 			"Updated Bucket",    // title
// 			BucketTagType.BE,    // BucketTagType
// 			LocalDateTime.now(), // dueDate
// 			BucketHowTo.MONEY,
// 			true,
// 			BigDecimal.valueOf(5000),
// 			true,
// 			BigDecimal.valueOf(1000),
// 			2000L,
// 			"0 0 12 * * ?",
// 			BigDecimal.valueOf(1000),
// 			BucketStatus.DOING,
// 			"Updated Memo",
// 			true,
// 			10L
// 		);
//
// 		// 기존 버킷 정보
// 		Bucket existingBucket = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.findById(1L)).thenReturn(Optional.of(existingBucket));
//
// 		// when: 업데이트 메서드를 호출
// 		BucketResponseDto.BucketInfo result = bucketService.updateBucket(1L, requestDto);
//
// 		// then: 결과 검증
// 		assertThat(result).isNotNull();
// 		assertThat(result.getTitle()).isEqualTo("Updated Bucket");
// 		assertThat(result.getMemo()).isEqualTo("Updated Memo");
//
// 		// findById 호출 검증
// 		verify(bucketRepository, times(1)).findById(1L);
//
// 		assertThat(existingBucket.getTitle()).isEqualTo("Updated Bucket");
// 		assertThat(existingBucket.getMemo()).isEqualTo("Updated Memo");
// 	}
//
// 	@Test
// 	void updateBucket_whenBucketNotFound_thenThrowsException() {
// 		Long bucketId = 1L;
//
// 		// 빌더를 사용해 객체 생성
// 		BucketRequestDto.Create requestDto = BucketRequestDto.Create.builder()
// 			.isRecommended(false)
// 			.originId(null)
// 			.build();
//
// 		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());
//
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.updateBucket(bucketId, requestDto));
// 		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
// 	}
//
// 	@Test
// 	void updateBucketStatus_ShouldUpdateBucketStatus() {
// 		User mockUser = new User();
//
// 		// given
// 		Bucket existingBucket = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.findById(1L)).thenReturn(Optional.of(existingBucket));
//
// 		// when
// 		BucketResponseDto.BucketInfo result = bucketService.updateBucketStatus(1L, Map.of("status", "DONE"));
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(existingBucket.getStatus()).isEqualTo(BucketStatus.DONE);
// 		verify(bucketRepository, times(1)).findById(1L);
// 		verify(bucketRepository, times(1)).save(existingBucket);
// 	}
//
// 	@Test
// 	void updateBucketStatus_throwsException_whenStatusIsMissing() {
// 		// Given
// 		Long bucketId = 1L;
// 		Map<String, String> updates = Map.of(); // status 값 없음
//
// 		// When & Then
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.updateBucketStatus(bucketId, updates));
// 		assertEquals("Status 값은 필수입니다.", exception.getMessage());
// 	}
//
// 	@Test
// 	void updateBucketStatus_whenStatusIsBlank_thenThrowsException() {
// 		Long bucketId = 1L;
// 		Map<String, String> updates = Map.of("status", " "); // status 값이 공백
//
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.updateBucketStatus(bucketId, updates));
// 		assertEquals("Status 값은 필수입니다.", exception.getMessage());
// 	}
//
// 	@Test
// 	void updateBucketStatus_whenStatusIsInvalid_thenThrowsException() {
// 		Long bucketId = 1L;
// 		Map<String, String> updates = Map.of("status", "INVALID_STATUS"); // 잘못된 상태값
//
// 		Bucket bucket = Bucket.builder().id(bucketId).status(BucketStatus.DOING).build();
// 		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(bucket));
//
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.updateBucketStatus(bucketId, updates));
// 		assertTrue(exception.getMessage().contains("No enum constant"));
// 	}
//
// 	@Test
// 	void updateBucketStatus_throwsException_whenBucketNotFound() { //버킷 없을때
// 		// Given
// 		Long bucketId = 1L;
// 		Map<String, String> updates = Map.of("status", "DONE");
//
// 		// `findById`가 빈 값을 반환하도록 설정
// 		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.updateBucketStatus(bucketId, updates));
// 		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
// 	}
//
// 	@Test
// 	void deleteBucket_ShouldDeleteBucket() {
// 		User mockUser = new User();
//
// 		// given
// 		Bucket existingBucket = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.findById(1L)).thenReturn(Optional.of(existingBucket));
//
// 		// when
// 		bucketService.deleteBucket(1L);
//
// 		// then
// 		verify(bucketRepository, times(1)).findById(1L);
// 		verify(bucketRepository, times(1)).delete(existingBucket);
// 	}
//
// 	@Test
// 	void deleteBucket_whenBucketNotFound_thenThrowsException() {
// 		// Given
// 		Long bucketId = 1L;
//
// 		// Mock: 버킷을 찾지 못했을 때 빈 Optional 반환
// 		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.deleteBucket(bucketId));
// 		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
//
// 		// bucketRepository.delete()가 호출되지 않았는지 확인
// 		verify(bucketRepository, never()).delete(any());
// 	}
//
// 	@Test
// 	void getAllBuckets_ShouldReturnAllBuckets() {
// 		User mockUser = new User();
//
// 		// given
// 		Bucket bucket1 = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
// 		Bucket bucket2 = new Bucket(1L, null, mockUser, "Bucket 2", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.findAll()).thenReturn(Arrays.asList(bucket1, bucket2));
//
// 		// when
// 		List<BucketResponseDto.BucketInfo> result = bucketService.getAllBuckets();
//
// 		// then
// 		assertThat(result).hasSize(2);
// 		assertThat(result.get(0).getTitle()).isEqualTo("Bucket 1");
// 		assertThat(result.get(1).getTitle()).isEqualTo("Bucket 2");
// 		verify(bucketRepository, times(1)).findAll();
// 	}
//
// 	@Test
// 	void getBucketById_ShouldReturnBucketById() {
// 		User mockUser = new User();
//
// 		// given
// 		Bucket existingBucket = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.findById(1L)).thenReturn(Optional.of(existingBucket));
//
// 		// when
// 		BucketResponseDto.BucketInfo result = bucketService.getBucketById(1L);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.getTitle()).isEqualTo("Bucket 1");
// 		verify(bucketRepository, times(1)).findById(1L);
// 	}
//
// 	@Test
// 	void getRecommendedBuckets_ShouldReturnTop3Recommendations() {
// 		User mockUser = new User();
//
// 		// given
// 		Bucket bucket1 = new Bucket(1L, null, mockUser, "Bucket 1", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 2000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
// 		Bucket bucket2 = new Bucket(2L, null, mockUser, "Bucket 2", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 3000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
// 		Bucket bucket3 = new Bucket(3L, null, mockUser, "Bucket 3", BucketTagType.BE, LocalDateTime.now(),
// 			true, "memo", BucketHowTo.MONEY, BigDecimal.valueOf(5000), 4000L, BucketStatus.DOING, true,
// 			BigDecimal.valueOf(1000), "0 0 12 * * ?", BigDecimal.valueOf(1000), true, 10L);
//
// 		when(bucketRepository.findAll()).thenReturn(Arrays.asList(bucket1, bucket2, bucket3));
//
// 		// when
// 		List<RecommendationResponseDto.RecommendedBucketInfo> recommendations = bucketService.getRecommendedBuckets();
//
// 		// then
// 		assertThat(recommendations).hasSize(1);
// 		assertThat(recommendations.get(0).getRecommendations()).hasSize(3);
// 		assertThat(recommendations.get(0).getRecommendations().get(0).getId()).isEqualTo(3L);
// 		assertThat(recommendations.get(0).getRecommendations().get(1).getId()).isEqualTo(2L);
// 		assertThat(recommendations.get(0).getRecommendations().get(2).getId()).isEqualTo(1L);
// 		verify(bucketRepository, times(1)).findAll();
// 	}
//
// 	@Test
// 	void getBucketById_whenBucketNotFound_thenThrowsException() {
// 		// Given
// 		Long bucketId = 1L;
//
// 		// Mock: 버킷이 없을 때 빈 Optional 반환
// 		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
// 			() -> bucketService.getBucketById(bucketId));
// 		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
// 	}
//
// }

package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.repository.BucketRepository;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {

	@Mock
	private BucketRepository bucketRepository;

	@InjectMocks
	private BucketService bucketService;

	@Test
	void getBucketsDueAfter_ReturnsCorrectBuckets() {
		// Given
		String userId = "testUser";
		LocalDate dueDate = LocalDate.of(2025, 1, 27);

		Bucket bucket1 = new Bucket();
		bucket1.setDueDate(LocalDateTime.now().plusDays(1));
		Bucket bucket2 = new Bucket();
		bucket2.setDueDate(LocalDateTime.now().plusYears(1));
		List<Bucket> expectedBuckets = Arrays.asList(bucket1, bucket2);

		when(bucketRepository.findByUserIdAndDueDateIsNullOrDueDateAfter(eq(userId), any(LocalDateTime.class)))
			.thenReturn(expectedBuckets);

		// When
		List<Bucket> result = bucketService.getBucketsDueAfter(userId, dueDate);

		// Then
		assertEquals(expectedBuckets.size(), result.size());
		assertEquals(expectedBuckets, result);
	}
}
