package com.hana4.ggumtle.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.dreamAccount.DreamAccountRequestDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.BucketRepository;
import com.hana4.ggumtle.repository.DreamAccountRepository;

@ExtendWith(MockitoExtension.class)
class DreamAccountServiceTest {

	@Mock
	private DreamAccountRepository dreamAccountRepository;

	@Mock
	private BucketRepository bucketRepository;

	@InjectMocks
	private DreamAccountService dreamAccountService;

	private User mockUser;
	private DreamAccount mockDreamAccount;
	private Bucket mockBucket;

	@BeforeEach
	void setUp() {
		mockUser = new User();
		mockUser.setId("1");

		mockDreamAccount = new DreamAccount();
		mockDreamAccount.setId(1L);
		mockDreamAccount.setBalance(new BigDecimal("10000"));
		mockDreamAccount.setTotal(new BigDecimal("50000"));
		mockDreamAccount.setUser(mockUser);

		mockBucket = new Bucket();
		mockBucket.setId(1L);
		mockBucket.setSafeBox(new BigDecimal("2000"));
		mockBucket.setDreamAccount(mockDreamAccount);
	}

	@Test
	void calculateTotalSafeBox_정상케이스() {
		// given
		Long dreamAccountId = 1L;

		DreamAccount mockDreamAccount = DreamAccount.builder()
			.id(dreamAccountId)
			.user(mockUser)
			.balance(new BigDecimal("10000"))
			.total(new BigDecimal("50000"))
			.build();

		Bucket bucket1 = Bucket.builder()
			.id(1L)
			.dreamAccount(mockDreamAccount)
			.safeBox(new BigDecimal("3000"))
			.build();

		Bucket bucket2 = Bucket.builder()
			.id(2L)
			.dreamAccount(mockDreamAccount)
			.safeBox(new BigDecimal("2000"))
			.build();

		Bucket bucket3 = Bucket.builder()
			.id(3L)
			.dreamAccount(mockDreamAccount)
			.safeBox(null)  // safeBox 값이 null인 경우
			.build();

		List<Bucket> bucketList = List.of(bucket1, bucket2, bucket3);

		when(bucketRepository.findAll()).thenReturn(bucketList);

		// when
		BigDecimal totalSafeBox = dreamAccountService.calculateTotalSafeBox(dreamAccountId);

		// then
		assertThat(totalSafeBox).isEqualTo(new BigDecimal("5000"));  // 3000 + 2000 + 0
		verify(bucketRepository, times(1)).findAll();
	}

	@Test
	void calculateTotalSafeBox_해당_꿈통장이_없을때() {
		// given
		Long dreamAccountId = 99L; // 존재하지 않는 ID

		Bucket bucket1 = Bucket.builder()
			.id(1L)
			.dreamAccount(mockDreamAccount) // 다른 DreamAccount ID
			.safeBox(new BigDecimal("3000"))
			.build();

		List<Bucket> bucketList = List.of(bucket1);

		when(bucketRepository.findAll()).thenReturn(bucketList);

		// when
		BigDecimal totalSafeBox = dreamAccountService.calculateTotalSafeBox(dreamAccountId);

		// then
		assertThat(totalSafeBox).isEqualTo(BigDecimal.ZERO);
		verify(bucketRepository, times(1)).findAll();
	}

	@Test
	void calculateTotalSafeBox_버킷리스트가_비어있을때() {
		// given
		Long dreamAccountId = 1L;

		when(bucketRepository.findAll()).thenReturn(Collections.emptyList());

		// when
		BigDecimal totalSafeBox = dreamAccountService.calculateTotalSafeBox(dreamAccountId);

		// then
		assertThat(totalSafeBox).isEqualTo(BigDecimal.ZERO);
		verify(bucketRepository, times(1)).findAll();
	}

	@Test
	void createDreamAccount_정상케이스() {
		// given
		DreamAccountRequestDto.Create requestDto = DreamAccountRequestDto.Create.builder()
			.balance(new BigDecimal("10000"))
			.total(new BigDecimal("50000"))
			.build();

		when(dreamAccountRepository.save(any(DreamAccount.class))).thenAnswer(invocation -> {
			DreamAccount da = invocation.getArgument(0);
			da.setId(1L); // 가짜 ID 설정
			return da;
		});

		// when
		DreamAccountResponseDto.DreamAccountInfo response = dreamAccountService.createDreamAccount(requestDto,
			mockUser);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getBalance()).isEqualTo(new BigDecimal("10000"));
		assertThat(response.getTotal()).isEqualTo(new BigDecimal("50000"));
		verify(dreamAccountRepository, times(1)).save(any(DreamAccount.class));
	}

	@Test
	void createDreamAccount_잘못된입력값() {
		// given
		DreamAccountRequestDto.Create requestDto = DreamAccountRequestDto.Create.builder()
			.balance(null) // balance가 null인 경우
			.total(new BigDecimal("50000"))
			.build();

		// when & then
		assertThrows(NullPointerException.class, () ->
			dreamAccountService.createDreamAccount(requestDto, mockUser));
	}

	@Test
	void createDreamAccount_유저정보누락() {
		// given
		DreamAccountRequestDto.Create requestDto = DreamAccountRequestDto.Create.builder()
			.balance(new BigDecimal("10000"))
			.total(new BigDecimal("50000"))
			.build();

		// when & then
		assertThrows(NullPointerException.class, () ->
			dreamAccountService.createDreamAccount(requestDto, null));
	}

	@Test
	void getDreamAccountByUserId_존재하는경우() {
		// given
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockDreamAccount));

		// when
		DreamAccountResponseDto.DreamAccountInfo response = dreamAccountService.getDreamAccountByUserId(
			mockUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.getBalance()).isEqualTo(mockDreamAccount.getBalance());
	}

	@Test
	void getDreamAccountByUserId_존재하지않는경우() {
		// given
		when(dreamAccountRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());

		// when
		DreamAccountResponseDto.DreamAccountInfo response = dreamAccountService.getDreamAccountByUserId(
			mockUser.getId());

		// then
		assertThat(response).isNull();
	}

	@Test
	void addAmountToDreamAccount_정상케이스() {
		// given
		BigDecimal amountToAdd = new BigDecimal("5000");
		when(dreamAccountRepository.findById(mockDreamAccount.getId())).thenReturn(Optional.of(mockDreamAccount));

		// when
		DreamAccountResponseDto.DreamAccountInfo response = dreamAccountService.addAmountToDreamAccount(
			mockDreamAccount.getId(), amountToAdd);

		// then
		assertThat(response.getBalance()).isEqualTo(new BigDecimal("15000"));
	}

	@Test
	void addAmountToDreamAccount_존재하지않는_꿈통장_예외발생() {
		// given
		Long nonExistentDreamAccountId = 999L; // 존재하지 않는 ID
		BigDecimal amount = new BigDecimal("1000");

		when(dreamAccountRepository.findById(nonExistentDreamAccountId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.addAmountToDreamAccount(nonExistentDreamAccountId, amount));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("꿈통장을 찾을 수 없습니다.");

		verify(dreamAccountRepository, times(1)).findById(nonExistentDreamAccountId);
		verifyNoMoreInteractions(dreamAccountRepository);
	}

	// @Test
	// void subtractAmountFromDreamAccount_잔액부족() {
	// 	// given
	// 	BigDecimal amountToSubtract = new BigDecimal("20000");
	// 	when(dreamAccountRepository.findById(mockDreamAccount.getId())).thenReturn(Optional.of(mockDreamAccount));
	//
	// 	// when & then
	// 	CustomException exception = assertThrows(CustomException.class,
	// 		() -> dreamAccountService.subtractAmountFromDreamAccount(mockDreamAccount.getId(), amountToSubtract));
	//
	// 	assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TRANSFER_FAILURE);
	// }

	@Test
	void distributeAmountToBucket_정상케이스() {
		// given
		BigDecimal amountToDistribute = new BigDecimal("5000");
		when(dreamAccountRepository.findById(mockDreamAccount.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));

		// when
		DreamAccountResponseDto.DreamAccountInfo response = dreamAccountService.distributeAmountToBucket(
			mockDreamAccount.getId(), mockBucket.getId(), amountToDistribute);

		// then
		assertThat(mockDreamAccount.getBalance()).isEqualTo(new BigDecimal("5000"));
		assertThat(mockBucket.getSafeBox()).isEqualTo(new BigDecimal("7000"));
	}

	@Test
	void distributeAmountToBucket_잔액부족() {
		// given
		BigDecimal amountToDistribute = new BigDecimal("20000");
		when(dreamAccountRepository.findById(mockDreamAccount.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> dreamAccountService.distributeAmountToBucket(mockDreamAccount.getId(), mockBucket.getId(),
				amountToDistribute));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TRANSFER_FAILURE);
	}

	@Test
	void distributeAmountToBucket_존재하지않는_꿈통장_예외발생() {
		// given
		Long nonExistentDreamAccountId = 999L; // 존재하지 않는 꿈통장 ID
		Long bucketId = 1L;
		BigDecimal amount = new BigDecimal("1000");

		when(dreamAccountRepository.findById(nonExistentDreamAccountId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.distributeAmountToBucket(nonExistentDreamAccountId, bucketId, amount));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("꿈통장을 찾을 수 없습니다.");

		verify(dreamAccountRepository, times(1)).findById(nonExistentDreamAccountId);
		verifyNoMoreInteractions(dreamAccountRepository);
	}

	@Test
	void distributeAmountToBucket_존재하지않는_버킷리스트_예외발생() {
		// given
		Long dreamAccountId = 1L;
		Long nonExistentBucketId = 999L; // 존재하지 않는 버킷리스트 ID
		BigDecimal amount = new BigDecimal("1000");

		DreamAccount mockDreamAccount = mock(DreamAccount.class);
		when(dreamAccountRepository.findById(dreamAccountId)).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.findById(nonExistentBucketId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.distributeAmountToBucket(dreamAccountId, nonExistentBucketId, amount));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("버킷리스트를 찾을 수 없습니다.");

		verify(dreamAccountRepository, times(1)).findById(dreamAccountId);
		verify(bucketRepository, times(1)).findById(nonExistentBucketId);
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void distributeAmountToDreamAccount_정상케이스() {
		// given
		BigDecimal amountToTransfer = new BigDecimal("1000");
		when(dreamAccountRepository.findById(mockDreamAccount.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));

		// when
		DreamAccountResponseDto.DreamAccountInfo response = dreamAccountService.distributeAmountToDreamAccount(
			mockDreamAccount.getId(), mockBucket.getId(), amountToTransfer);

		// then
		assertThat(mockDreamAccount.getBalance()).isEqualTo(new BigDecimal("11000"));
		assertThat(mockBucket.getSafeBox()).isEqualTo(new BigDecimal("1000"));
	}

	@Test
	void distributeAmountToDreamAccount_존재하지않는_꿈통장_예외발생() {
		// given
		Long nonExistentDreamAccountId = 999L; // 존재하지 않는 꿈통장 ID
		Long bucketId = 1L;
		BigDecimal amount = new BigDecimal("1000");

		when(dreamAccountRepository.findById(nonExistentDreamAccountId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.distributeAmountToDreamAccount(nonExistentDreamAccountId, bucketId, amount));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("꿈통장을 찾을 수 없습니다.");

		verify(dreamAccountRepository, times(1)).findById(nonExistentDreamAccountId);
		verifyNoMoreInteractions(dreamAccountRepository);
	}

	@Test
	void distributeAmountToDreamAccount_존재하지않는_버킷리스트_예외발생() {
		// given
		Long dreamAccountId = 1L;
		Long nonExistentBucketId = 999L; // 존재하지 않는 버킷리스트 ID
		BigDecimal amount = new BigDecimal("1000");

		DreamAccount mockDreamAccount = mock(DreamAccount.class);
		when(dreamAccountRepository.findById(dreamAccountId)).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.findById(nonExistentBucketId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.distributeAmountToDreamAccount(dreamAccountId, nonExistentBucketId, amount));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("버킷리스트를 찾을 수 없습니다.");

		verify(dreamAccountRepository, times(1)).findById(dreamAccountId);
		verify(bucketRepository, times(1)).findById(nonExistentBucketId);
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void distributeAmountToDreamAccount_버킷잔액부족() {
		// given
		BigDecimal amountToTransfer = new BigDecimal("5000");
		when(dreamAccountRepository.findById(mockDreamAccount.getId())).thenReturn(Optional.of(mockDreamAccount));
		when(bucketRepository.findById(mockBucket.getId())).thenReturn(Optional.of(mockBucket));

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> dreamAccountService.distributeAmountToDreamAccount(mockDreamAccount.getId(), mockBucket.getId(),
				amountToTransfer));

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TRANSFER_FAILURE);
	}

	@Test
	void subtractAmountFromDreamAccount_정상케이스() {
		// given
		Long dreamAccountId = 1L;
		BigDecimal initialBalance = new BigDecimal("10000");
		BigDecimal initialTotal = new BigDecimal("50000");
		BigDecimal subtractAmount = new BigDecimal("5000");

		DreamAccount mockDreamAccount = DreamAccount.builder()
			.id(dreamAccountId)
			.user(mockUser)
			.balance(initialBalance)
			.total(initialTotal)
			.build();

		when(dreamAccountRepository.findById(dreamAccountId)).thenReturn(Optional.of(mockDreamAccount));
		when(dreamAccountRepository.save(any(DreamAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		DreamAccountResponseDto.DreamAccountInfo response =
			dreamAccountService.subtractAmountFromDreamAccount(dreamAccountId, subtractAmount);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getBalance()).isEqualTo(initialBalance.subtract(subtractAmount));
		assertThat(response.getTotal()).isEqualTo(initialTotal.subtract(subtractAmount));
		verify(dreamAccountRepository, times(1)).findById(dreamAccountId);
		verify(dreamAccountRepository, times(1)).save(any(DreamAccount.class));
	}

	@Test
	void subtractAmountFromDreamAccount_존재하지않는꿈통장() {
		// given
		Long dreamAccountId = 999L;
		BigDecimal subtractAmount = new BigDecimal("5000");

		when(dreamAccountRepository.findById(dreamAccountId)).thenReturn(Optional.empty());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.subtractAmountFromDreamAccount(dreamAccountId, subtractAmount)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
		verify(dreamAccountRepository, times(1)).findById(dreamAccountId);
	}

	@Test
	void subtractAmountFromDreamAccount_잔액부족() {
		// given
		Long dreamAccountId = 1L;
		BigDecimal initialBalance = new BigDecimal("3000");
		BigDecimal initialTotal = new BigDecimal("10000");
		BigDecimal subtractAmount = new BigDecimal("5000"); // 잔액보다 큰 금액

		DreamAccount mockDreamAccount = DreamAccount.builder()
			.id(dreamAccountId)
			.user(mockUser)
			.balance(initialBalance)
			.total(initialTotal)
			.build();

		when(dreamAccountRepository.findById(dreamAccountId)).thenReturn(Optional.of(mockDreamAccount));

		// when & then
		CustomException exception = assertThrows(CustomException.class, () ->
			dreamAccountService.subtractAmountFromDreamAccount(dreamAccountId, subtractAmount)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TRANSFER_FAILURE);
		verify(dreamAccountRepository, times(1)).findById(dreamAccountId);
	}

}