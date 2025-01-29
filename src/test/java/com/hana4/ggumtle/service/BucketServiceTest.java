package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.BucketRepository;
import com.hana4.ggumtle.repository.DreamAccountRepository;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {

	@Mock
	private BucketRepository bucketRepository;

	@Mock
	private DreamAccountRepository dreamAccountRepository;

	@InjectMocks
	private BucketService bucketService;

	private User mockUser;
	private Bucket mockBucket;
	private DreamAccount mockDreamAccount;

	@BeforeEach
	void setUp() {
		mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();

		mockDreamAccount = DreamAccount.builder()
			.id(1L)
			.user(mockUser)
			.build();

		mockBucket = Bucket.builder()
			.id(1L)
			.title("New Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDate.parse("2025-01-21").atStartOfDay())
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.memo("여행 꼭 가고싶다")
			.followers(300L)
			.cronCycle("0 0 1 * *")
			.user(mockUser)
			.dreamAccount(mockDreamAccount)
			.build();
	}

	@Test
	void createBucket_Success() {
		// Given: 요청 DTO와 Mock 데이터 준비
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("New Bucket")
			.tagType(BucketTagType.GO)
			.dueDate("2025-01-21")
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isRecommended(false)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.memo("여행 꼭 가고싶다")
			.followers(300L)
			.cronCycle("0 0 1 * *")
			.build();

		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();

		// When: Repository 동작 모킹
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.save(Mockito.any(Bucket.class))).thenReturn(mockBucket);

		// When: 서비스 호출
		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, mockUser);

		// Then: 결과 검증
		assertNotNull(result, "BucketInfo 객체가 null로 반환되었습니다.");
		assertEquals(mockBucket.getId(), result.getId());
		assertEquals(mockBucket.getTitle(), result.getTitle());
		assertEquals(mockBucket.getUser().getId(), result.getUserId());
		assertEquals(mockBucket.getTagType(), result.getTagType());
		assertEquals(mockBucket.getDueDate(), result.getDueDate());
		assertEquals(mockBucket.getMemo(), result.getMemo());
		assertEquals(mockBucket.getGoalAmount(), result.getGoalAmount());

		// Verify: Repository 메서드 호출 확인
		Mockito.verify(bucketRepository, Mockito.times(1)).save(Mockito.any(Bucket.class));
		Mockito.verify(dreamAccountRepository, Mockito.times(1)).findByUserId(mockUser.getId());
	}

	@Test
	void createBucket_DreamAccountNotFound() {
		// Given: 요청 DTO 준비
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("New Bucket")
			.build();

		// 임의의 user 객체 설정 (예시로 이전에 설정한 mockUser를 사용)

		// When: dreamAccountRepository에서 해당 user의 DreamAccount를 찾을 수 없을 경우 (Optional.empty() 반환)
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());

		// Then: CustomException이 발생하고, ErrorCode.NOT_FOUND를 포함하는지 확인
		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.createBucket(requestDto, mockUser));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);

		// Verify: bucketRepository.save()가 호출되지 않았는지 확인
		verify(bucketRepository, never()).save(any(Bucket.class));
	}

	@Test
	void 추천버킷_OriginId존재_예외발생() {
		// Given

		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("추천 버킷")
			.isRecommended(true)
			.originId(1L) // 추천 버킷이 originId를 가질 수 없음
			.followers(50L)
			.build();

		// When & Then
		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.createBucket(requestDto, mockUser));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAMETER);
	}

	@Test
	void testCreateBucket_RecommendedBucketWithoutFollowers() {
		// given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.isRecommended(true)  // Make this a recommended bucket
			.followers(null)      // followers is null, which should trigger the exception
			.build();
		User user = mockUser;  // Using the mockUser defined in your setup

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			bucketService.createBucket(requestDto, user);  // Call the method
		});

		// Validate that the exception is thrown with the expected error code and message
		assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());
		assertEquals("추천하는 버킷은 followers를 가져야 합니다.", exception.getMessage());
	}

	@Test
	void testCreateBucket_RecommendedBucket_Success() {
		// given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("New Bucket")
			.tagType(BucketTagType.GO)
			.dueDate("2025-01-21")
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isRecommended(true)  // 추천 버킷
			.originId(null)       // originId 없음 (정상적인 경우)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.memo("여행 꼭 가고싶다")
			.followers(300L)      // followers 값 있음 (정상)
			.cronCycle("0 0 1 * *")
			.build();

		User user = mockUser; // 테스트에서 사용할 mockUser
		DreamAccount mockDreamAccount = mock(DreamAccount.class);

		// Mocking: DreamAccountRepository가 정상적으로 DreamAccount를 반환하도록 설정
		when(dreamAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(mockDreamAccount));

		// Mocking: BucketRepository가 정상적으로 버킷을 저장하고 반환하도록 설정
		when(bucketRepository.save(any(Bucket.class))).thenAnswer(invocation -> {
			Bucket savedBucket = invocation.getArgument(0);
			savedBucket.setId(1L); // 저장 후 ID가 1L로 설정된다고 가정
			return savedBucket;
		});

		// when
		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, user);

		// then
		assertNotNull(result);
		assertEquals("New Bucket", result.getTitle());
		assertEquals(BucketTagType.GO, result.getTagType());
		assertEquals(300L, result.getFollowers());
	}

	@Test
	void 추천된버킷에서_새로운버킷생성_정상작동() {
		// Given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("추천된 버킷 복사")
			.originId(1L) // 기존 추천 버킷의 ID
			.isRecommended(false)
			.build();

		when(bucketRepository.findById(1L)).thenReturn(Optional.of(mockBucket));
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.save(any(Bucket.class))).thenReturn(mockBucket);

		// When
		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, mockUser);

		// Then
		assertNotNull(result);
		assertEquals(mockBucket.getTitle(), result.getTitle());
		assertEquals(301L, mockBucket.getFollowers()); // followers 증가 확인
		verify(bucketRepository, times(1)).save(mockBucket);
	}

	@Test
	void 추천된버킷에서_OriginBucket없음_예외발생() {
		// Given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("추천된 버킷 복사")
			.originId(999L) // 존재하지 않는 OriginId
			.isRecommended(false)
			.build();

		when(bucketRepository.findById(999L)).thenReturn(Optional.empty());

		// When & Then
		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.createBucket(requestDto, mockUser));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
	}

	@Test
	void 일반버킷_생성_정상작동() {
		// Given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("New Bucket")
			.isRecommended(false)
			.build();

		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.save(any(Bucket.class))).thenReturn(mockBucket);

		// When
		BucketResponseDto.BucketInfo result = bucketService.createBucket(requestDto, mockUser);

		// Then
		assertNotNull(result);
		assertEquals(requestDto.getTitle(), result.getTitle());
		verify(bucketRepository, times(1)).save(any(Bucket.class));
	}

	@Test
	void 일반버킷_DreamAccount없음_예외발생() {
		// Given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("New Bucket")
			.build();

		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());

		// When & Then
		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.createBucket(requestDto, mockUser));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
	}

	@Test
	void updateBucket_Success() {
		// Given: 요청 DTO 및 Mock 객체 준비
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("Updated Bucket")
			.build();

		// Mock Bucket 객체 설정
		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.user(mockUser)
			.title("Original Bucket")
			.build();

		// When: Repository 동작 모킹
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));

		// 서비스 호출
		BucketResponseDto.BucketInfo result = bucketService.updateBucket(mockUser, mockBucket.getId(), requestDto);

		// Then: 결과 검증
		assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());

		// Verify: Repository 메서드 호출 확인
		verify(bucketRepository, times(1)).save(any(Bucket.class));
	}

	@Test
	void updateBucket_UnauthorizedUser() {
		Long bucketId = 2L;

		User anotherUser = User.builder()
			.id("2")
			.tel("010-7777-8888")
			.password("password")
			.name("박철수")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1994, 5, 15, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile2.jpg")
			.nickname("anotherUser")
			.build();

		Bucket anotherBucket = Bucket.builder()
			.id(bucketId)
			.user(anotherUser)
			.title("Another Bucket")
			.build();

		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("Updated Bucket")
			.build();

		Mockito.when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(anotherBucket));

		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.updateBucket(mockUser, bucketId, requestDto));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

		Mockito.verify(bucketRepository, Mockito.never()).save(Mockito.any(Bucket.class));
	}

	@Test
	void updateBucket_존재하지않는버킷_예외발생() {
		// Given
		when(bucketRepository.findById(anyLong())).thenReturn(Optional.empty());

		Long bucketId = 1L;

		// When & Then
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("Updated Bucket")
			.build();

		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.updateBucket(mockUser, bucketId, requestDto));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
	}

	@Test
	void testUpdateBucket_InvalidDueDateFormat() {
		// given
		Long bucketId = 1L;
		BucketRequestDto.CreateBucket invalidRequestDto = BucketRequestDto.CreateBucket
			.builder()
			.dueDate("invalid-date") // 잘못된 날짜 형식
			.build();

		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));

		// when & then
		assertThrows(DateTimeParseException.class, () -> {
			bucketService.updateBucket(mockUser, bucketId, invalidRequestDto);
		});
	}

	@Test
	void testUpdateBucket_ValidDueDate() {
		// given
		Long bucketId = 1L;
		String validDueDate = "2025-01-21";  // 유효한 날짜 형식
		BucketRequestDto.CreateBucket validRequestDto = BucketRequestDto.CreateBucket
			.builder()
			.dueDate(validDueDate)  // 유효한 날짜
			.build();

		// mock 객체 설정
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));

		// when
		BucketResponseDto.BucketInfo result = bucketService.updateBucket(mockUser, bucketId, validRequestDto);

		// then
		assertNotNull(result);  // 결과가 null이 아님을 확인
		verify(bucketRepository, times(1)).findById(bucketId);  // bucketRepository의 findById 메서드가 1번 호출되었는지 검증
		verify(dreamAccountRepository, times(1)).findByUserId(
			mockUser.getId());  // dreamAccountRepository의 findByUserId 메서드가 1번 호출되었는지 검증
		verify(bucketRepository, times(1)).save(any(Bucket.class));  // bucketRepository의 save 메서드가 1번 호출되었는지 검증
	}

	@Test
	void 버킷_업데이트_시_DreamAccount_찾을_수_없을_때_예외가_발생한다() {
		// Given
		Long bucketId = 1L;
		BucketRequestDto.CreateBucket requestDto = createMockRequestDtoWithoutDueDate(); // dueDate가 없는 경우
		Bucket existingBucket = mock(Bucket.class); // 기존 버킷 mock
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(existingBucket));
		when(existingBucket.getUser()).thenReturn(mockUser);

		// Mocking DreamAccount repository to return empty
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(CustomException.class, () -> {
			bucketService.updateBucket(mockUser, bucketId, requestDto);
		}, "DreamAccount를 찾을 수 없습니다."); // DreamAccount가 없을 때 예외가 발생해야 함
	}

	@Test
	void 버킷_완료날짜가_잘못된_형식일_때_예외발생() {
		// Given
		Long bucketId = 1L;
		BucketRequestDto.CreateBucket requestDto = createMockRequestDtoWithValidDueDate("invalid-date");

		// When & Then
		assertThrows(CustomException.class,
			() -> bucketService.updateBucket(mockUser, bucketId, requestDto)); // ✅ 형식이 잘못된 경우 예외 발생
	}

	// Mock CreateBucket DTO (dueDate가 null)
	private BucketRequestDto.CreateBucket createMockRequestDtoWithoutDueDate() {
		return BucketRequestDto.CreateBucket.builder()
			.title("테스트 버킷")
			.dueDate(null) // ✅ dueDate가 null
			.build();
	}

	// Mock CreateBucket DTO (valid dueDate)
	private BucketRequestDto.CreateBucket createMockRequestDtoWithValidDueDate(String dueDate) {
		return BucketRequestDto.CreateBucket.builder()
			.title("테스트 버킷")
			.dueDate(dueDate) // ✅ 정상적인 날짜
			.build();
	}

	@Test
	void testUpdateBucketStatus_BucketNotFound() {
		// given
		Long bucketId = 1L;
		BucketRequestDto.UpdateBucketStatus updates = BucketRequestDto.UpdateBucketStatus
			.builder()
			.status(BucketStatus.DONE)  // 상태를 DONE으로 변경
			.build();

		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());  // 버킷이 존재하지 않음

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			bucketService.updateBucketStatus(mockUser, bucketId, updates);
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
	}

	@Test
	void testUpdateBucketStatus_InvalidUserPermission() {
		// given
		Long bucketId = 1L;
		BucketRequestDto.UpdateBucketStatus updates = BucketRequestDto.UpdateBucketStatus
			.builder()
			.status(BucketStatus.DONE)  // 상태를 DONE으로 설정
			.build();

		// mockBucket은 다른 사용자로 설정하여 권한 없는 사용자로 처리
		Bucket mockBucket = new Bucket();
		User anotherUser = User.builder()
			.id("2")
			.tel("010-7777-8888")
			.password("password")
			.name("박철수")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1994, 5, 15, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile2.jpg")
			.nickname("anotherUser")
			.build();
		mockBucket.setUser(anotherUser);  // anotherUser는 권한이 없는 다른 사용자

		// bucketRepository가 mockBucket을 반환하도록 설정
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));

		// bucketService를 spy로 감싸서 checkValidUser가 호출될 수 있도록 설정
		BucketService bucketServiceSpy = Mockito.spy(bucketService);
		when(bucketServiceSpy.checkValidUser(mockUser, mockBucket)).thenReturn(false);  // 권한이 없으면 false 반환

		// when & then
		assertThrows(CustomException.class, () -> {
			bucketServiceSpy.updateBucketStatus(mockUser, bucketId, updates);
		});
	}

	@Test
	void testUpdateBucketStatus_ValidUserPermission() {
		// given
		Long bucketId = 1L;
		BucketRequestDto.UpdateBucketStatus updates = BucketRequestDto.UpdateBucketStatus
			.builder()
			.status(BucketStatus.DONE)  // 상태를 DONE으로 설정
			.build();

		// mockBucket은 권한이 있는 사용자로 설정
		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();

		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.title("New Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDate.parse("2025-01-21").atStartOfDay())
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.memo("여행 꼭 가고싶다")
			.followers(300L)
			.cronCycle("0 0 1 * *")
			.user(mockUser)
			.dreamAccount(mockDreamAccount)
			.build();
		;

		// bucketRepository가 mockBucket을 반환하도록 설정
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));

		// bucketService를 spy로 감싸서 checkValidUser가 호출될 수 있도록 설정
		BucketService bucketServiceSpy = Mockito.spy(bucketService);
		when(bucketServiceSpy.checkValidUser(mockUser, mockBucket)).thenReturn(true);  // 권한이 있으면 true 반환

		// when & then
		BucketResponseDto.BucketInfo result = bucketServiceSpy.updateBucketStatus(mockUser, bucketId, updates);

		// 버킷 상태가 DONE으로 변경되었는지 확인
		assertNotNull(result);
		assertEquals(BucketStatus.DONE, result.getStatus());  // 상태가 DONE이어야 함
	}

	@Test
	void deleteBucket_Success() {
		// Given: Mock 객체 준비
		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();

		// Mock DreamAccount 객체 설정
		DreamAccount mockDreamAccount = DreamAccount.builder()
			.id(1L)
			.user(mockUser)
			.balance(new BigDecimal("1000.00"))
			.total(new BigDecimal("2000.00"))
			.build();

		// Mock Bucket 객체 설정 (dreamAccount 포함)
		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.user(mockUser)
			.dreamAccount(mockDreamAccount)  // DreamAccount 설정
			.title("New Bucket")
			.build();

		// When: Repository 동작 모킹
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));

		// 서비스 호출
		bucketService.deleteBucket(mockBucket.getId(), mockUser);

		// Then: Repository의 delete 메서드가 한 번 호출되었는지 확인
		verify(bucketRepository, times(1)).delete(mockBucket);
	}

	@Test
	void deleteBucket_삭제권한없음_예외발생() {
		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();

		// Mock DreamAccount 객체 설정
		DreamAccount mockDreamAccount = DreamAccount.builder()
			.id(1L)
			.user(mockUser)
			.balance(new BigDecimal("1000.00"))
			.total(new BigDecimal("2000.00"))
			.build();
		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.user(mockUser)
			.dreamAccount(mockDreamAccount)  // DreamAccount 설정
			.title("New Bucket")
			.build();
		// Given
		User otherUser = User.builder().id("2").name("다른 유저").build();
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));

		// When & Then
		CustomException exception = assertThrows(CustomException.class,
			() -> bucketService.deleteBucket(mockBucket.getId(), otherUser));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
	}

	@Test
	void testDeleteBucket_NotFound() {
		// given
		Long bucketId = 999L; // Assuming this ID does not exist in the repository
		User mockUser = new User(); // Use an actual mock or your pre-defined mock user

		// Mock the bucketRepository to return an empty Optional, simulating a bucket not found
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());

		// when & then
		// Verify that CustomException is thrown with the expected error message
		CustomException exception = assertThrows(CustomException.class, () -> {
			bucketService.deleteBucket(bucketId, mockUser);
		});

		// Assert that the exception message is correct
		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
		assertEquals(ErrorCode.NOT_FOUND,
			exception.getErrorCode());  // Assuming you have this method in CustomException
	}

	@Test
	void getAllBuckets_Success() {
		// Given: User와 DreamAccount, Bucket 객체 초기화
		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();

		DreamAccount mockDreamAccount = DreamAccount.builder()
			.id(1L)
			.user(mockUser)
			.balance(BigDecimal.ZERO)  // 적절한 값 설정
			.total(BigDecimal.ZERO)    // 적절한 값 설정
			.build();

		// Mock Bucket 객체 설정
		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.user(mockUser)           // User 객체 설정
			.dreamAccount(mockDreamAccount)  // DreamAccount 객체 설정
			.title("New Bucket")
			.build();

		// Bucket 목록 생성
		List<Bucket> bucketList = new ArrayList<>();
		bucketList.add(mockBucket);

		// When: Repository 동작 모킹
		when(bucketRepository.findAllByUserId(mockUser.getId())).thenReturn(bucketList);

		// 서비스 호출
		List<BucketResponseDto.BucketInfo> result = bucketService.getAllBuckets(mockUser);

		// Then: 결과 검증
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo(mockBucket.getTitle());
	}

	@Test
	void testGetBucketById_Success() {
		// given
		Long bucketId = 1L;

		// mock bucketRepository to return mockBucket when the findById method is called
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));

		// when
		BucketResponseDto.BucketInfo result = bucketService.getBucketById(bucketId);

		// then
		assertNotNull(result);
		assertEquals(mockBucket.getId(), result.getId());
		assertEquals(mockBucket.getTitle(), result.getTitle());
		// You can add more assertions to verify other fields of result as needed.
	}

	@Test
	void testGetBucketById_BucketNotFound() {
		// given
		Long bucketId = 1L;

		// mock bucketRepository to return an empty Optional, simulating a bucket not found
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CustomException.class, () -> {
			bucketService.getBucketById(bucketId);
		});
	}

	@Test
	void testGetRecommendedBuckets_WithTagType() {
		// given
		BucketTagType tagType = BucketTagType.GO;
		List<Bucket> mockRecommendedBuckets = Arrays.asList(mockBucket, mockBucket);

		// Mock the repository method to return the list of recommended buckets for the specific tag type
		when(bucketRepository.findByTagTypeAndIsRecommendedTrue(tagType)).thenReturn(mockRecommendedBuckets);

		// when
		List<RecommendationResponseDto.RecommendedBucketInfo> result = bucketService.getRecommendedBuckets(tagType);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
		// Add more assertions to check the specific fields of the result as needed
	}

	@Test
	void testGetRecommendedBuckets_WithNullTagType() {
		// given
		BucketTagType tagType = null;

		// Create mock Bucket objects
		Bucket mockBucket1 = mock(Bucket.class);
		Bucket mockBucket2 = mock(Bucket.class);

		// Set up mock behavior for mockBucket1 and mockBucket2
		when(mockBucket1.getTagType()).thenReturn(BucketTagType.GO);
		when(mockBucket1.getFollowers()).thenReturn(300L);

		when(mockBucket2.getTagType()).thenReturn(BucketTagType.DO);
		when(mockBucket2.getFollowers()).thenReturn(150L);

		List<Bucket> mockRecommendedBuckets = Arrays.asList(mockBucket1, mockBucket2);

		// Mock the repository method to return the list of recommended buckets
		when(bucketRepository.findByIsRecommendedTrue()).thenReturn(mockRecommendedBuckets);

		// when
		List<RecommendationResponseDto.RecommendedBucketInfo> result = bucketService.getRecommendedBuckets(tagType);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());

		// You can add more assertions to check specific fields of the result
		// Example:
		assertEquals(2, result.size());  // Expecting 2 recommended buckets
	}

	@Test
	void testGetRecommendedBuckets_SortingAndLimitingTop3() {
		// given
		BucketTagType tagType = null;
		Bucket bucket1 = Bucket.builder()
			.id(1L)
			.title("Bucket 1")
			.followers(100L)
			.tagType(BucketTagType.GO)
			.build();
		Bucket bucket2 = Bucket.builder()
			.id(2L)
			.title("Bucket 2")
			.followers(300L)
			.tagType(BucketTagType.GO)
			.build();
		Bucket bucket3 = Bucket.builder()
			.id(3L)
			.title("Bucket 3")
			.followers(200L)
			.tagType(BucketTagType.GO)
			.build();
		Bucket bucket4 = Bucket.builder()
			.id(4L)
			.title("Bucket 4")
			.followers(150L)
			.tagType(BucketTagType.GO)
			.build();

		List<Bucket> mockRecommendedBuckets = Arrays.asList(bucket1, bucket2, bucket3, bucket4);

		// Mock the repository to return the recommended buckets
		when(bucketRepository.findByIsRecommendedTrue()).thenReturn(mockRecommendedBuckets);

		// when
		List<RecommendationResponseDto.RecommendedBucketInfo> result = bucketService.getRecommendedBuckets(tagType);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());

		// Get the first (and only) group since tagType is null
		RecommendationResponseDto.RecommendedBucketInfo recommendedBucketInfo = result.get(0);

		// Check that the group contains the top 3 recommended buckets by followers
		List<RecommendationResponseDto.RecommendedBucketInfo.Recommendation> topRecommendations = recommendedBucketInfo.getRecommendations();

		assertEquals(3, topRecommendations.size());  // Check that only 3 recommendations are included
		assertEquals(300L, topRecommendations.get(0).getFollowers());  // Highest followers first (Bucket 2)
		assertEquals(200L, topRecommendations.get(1).getFollowers());  // Bucket 3
		assertEquals(150L, topRecommendations.get(2).getFollowers());  // Bucket 4
	}

	@Test
	void testGetBucket_Success() {
		// given
		Long bucketId = 1L;

		// Mock a Bucket object that should be returned when the bucket is found
		Bucket mockBucket = Bucket.builder()
			.id(bucketId)
			.title("Test Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDate.parse("2025-01-21").atStartOfDay())
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.memo("Test Memo")
			.followers(300L)
			.cronCycle("0 0 1 * *")
			.user(mockUser) // Assuming mockUser is set up as part of your test
			.dreamAccount(mockDreamAccount) // Assuming mockDreamAccount is set up as part of your test
			.build();

		// Mock the bucketRepository to return the mock bucket when findById is called
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));

		// when
		Bucket result = bucketService.getBucket(bucketId);

		// then
		assertNotNull(result);
		assertEquals(bucketId, result.getId());
		assertEquals("Test Bucket", result.getTitle());
	}

	@Test
	void testGetBucket_NotFound() {
		// given
		Long bucketId = 999L; // Assuming this ID does not exist in the repository

		// Mock the bucketRepository to return an empty Optional, simulating a bucket not found
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.empty());

		// when & then
		// Verify that CustomException is thrown with the expected error message
		CustomException exception = assertThrows(CustomException.class, () -> {
			bucketService.getBucket(bucketId);
		});

		// Assert that the exception message is correct
		assertEquals("버킷을 찾을 수 없습니다.", exception.getMessage());
		assertEquals(ErrorCode.NOT_FOUND,
			exception.getErrorCode());  // Assuming you have this method in CustomException
	}

	@Test
	void testGetBucketsDueAfter_Success() {
		// given
		String userId = mockUser.getId();  // "1"
		LocalDate dueDate = LocalDate.of(2025, 1, 1);
		LocalDateTime startOfDay = dueDate.atStartOfDay();

		Bucket bucket1 = Bucket.builder()
			.id(1L)
			.title("Bucket 1")
			.howTo(BucketHowTo.MONEY)
			.dueDate(null)  // dueDate가 null인 경우 (조건 충족)
			.user(mockUser)
			.build();

		Bucket bucket2 = Bucket.builder()
			.id(2L)
			.title("Bucket 2")
			.howTo(BucketHowTo.MONEY)
			.dueDate(LocalDate.of(2025, 2, 1).atStartOfDay())  // 2025-01-01 이후 (조건 충족)
			.user(mockUser)
			.build();

		Bucket bucket3 = Bucket.builder()
			.id(3L)
			.title("Bucket 3")
			.howTo(BucketHowTo.MONEY)
			.dueDate(LocalDate.of(2024, 12, 31).atStartOfDay())  // 2025-01-01 이전 (조건 불충족)
			.user(mockUser)
			.build();

		List<Bucket> mockBuckets = Arrays.asList(bucket1, bucket2);

		// Mocking: 버킷 레포지토리가 특정 조건의 버킷 리스트를 반환하도록 설정
		when(bucketRepository.findByUserIdAndHowToEqualsAndDueDateIsNullOrDueDateAfter(userId,
			BucketHowTo.MONEY, startOfDay))
			.thenReturn(mockBuckets);

		// when
		List<Bucket> result = bucketService.getBucketsDueAfter(userId, dueDate);

		// then
		assertNotNull(result);
		assertEquals(2, result.size());  // 조건을 만족하는 2개의 버킷이 반환되어야 함
		assertTrue(result.contains(bucket1));
		assertTrue(result.contains(bucket2));
	}

	@Test
	void testUpdateBucketStatus_SetSafeBoxToZero_WhenStatusIsDone() {
		// given
		Long bucketId = mockBucket.getId();  // 1L
		BucketRequestDto.UpdateBucketStatus updates = new BucketRequestDto.UpdateBucketStatus(BucketStatus.DONE);

		// 기존 버킷 상태
		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();
		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.title("New Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDate.parse("2025-01-21").atStartOfDay())
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.memo("여행 꼭 가고싶다")
			.followers(300L)
			.cronCycle("0 0 1 * *")
			.user(mockUser)
			.safeBox(new BigDecimal("500.00"))
			.dreamAccount(mockDreamAccount)
			.build();

		// Mocking: 특정 버킷 ID로 조회할 때 `bucket`이 반환되도록 설정
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));
		BucketService bucketServiceSpy = Mockito.spy(bucketService);

		// Mocking: checkValidUser가 true를 반환하도록 설정 (유효한 사용자)
		when(bucketServiceSpy.checkValidUser(mockUser, mockBucket)).thenReturn(true);

		// when
		BucketResponseDto.BucketInfo result = bucketServiceSpy.updateBucketStatus(mockUser, bucketId, updates);

		// then
		assertNotNull(result);
		assertEquals(BucketStatus.DONE, mockBucket.getStatus());  // 상태가 DONE으로 변경되었는지 확인
		assertEquals(BigDecimal.ZERO, mockBucket.getSafeBox());   // safeBox가 0으로 변경되었는지 확인

		// 저장이 호출되었는지 검증
		verify(bucketRepository, times(1)).save(mockBucket);
	}

	@Test
	void testUpdateBucketStatus_SafeBoxUnchanged_WhenStatusIsNotDone() {
		// given
		Long bucketId = mockBucket.getId();  // 1L
		BucketRequestDto.UpdateBucketStatus updates = new BucketRequestDto.UpdateBucketStatus(BucketStatus.HOLD);

		// 기존 버킷 상태 (safeBox 값이 존재)
		User mockUser = User.builder()
			.id("1")
			.tel("010-5555-6666")
			.password("password")
			.name("최강희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1999, 7, 30, 0, 0))
			.gender("M")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("somsomsomsom")
			.build();
		Bucket mockBucket = Bucket.builder()
			.id(1L)
			.title("New Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDate.parse("2025-01-21").atStartOfDay())
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(true)
			.allocateAmount(new BigDecimal("500.00"))
			.goalAmount(new BigDecimal("1000000"))
			.safeBox(new BigDecimal("500.00"))
			.memo("여행 꼭 가고싶다")
			.followers(300L)
			.cronCycle("0 0 1 * *")
			.user(mockUser)
			.dreamAccount(mockDreamAccount)
			.build();

		// Mocking: 특정 버킷 ID로 조회할 때 `bucket`이 반환되도록 설정
		when(bucketRepository.findById(bucketId)).thenReturn(Optional.of(mockBucket));
		BucketService bucketServiceSpy = Mockito.spy(bucketService);

		// Mocking: checkValidUser가 true를 반환하도록 설정 (유효한 사용자)
		when(bucketServiceSpy.checkValidUser(mockUser, mockBucket)).thenReturn(true);

		// when
		BucketResponseDto.BucketInfo result = bucketServiceSpy.updateBucketStatus(mockUser, bucketId, updates);

		// then
		assertNotNull(result);
		assertEquals(BucketStatus.HOLD, mockBucket.getStatus());
		System.out.println();
		assertEquals(new BigDecimal("500.00"), mockBucket.getSafeBox()); // safeBox 값이 그대로인지 확인

		// 저장이 호출되었는지 검증
		verify(bucketRepository, times(1)).save(mockBucket);
	}

}