package com.hana4.ggumtle.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.dreamAccount.DreamAccountRequestDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.BucketRepository;
import com.hana4.ggumtle.repository.DreamAccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DreamAccountService {

	private final DreamAccountRepository dreamAccountRepository;
	private final BucketRepository bucketRepository;

	public BigDecimal calculateTotalSafeBox(Long dreamAccountId) {
		List<Bucket> buckets = bucketRepository.findAll();
		return buckets.stream()
			.filter(bucket -> bucket.getDreamAccount().getId().equals(dreamAccountId))
			.map(bucket -> bucket.getSafeBox() != null ? bucket.getSafeBox() : BigDecimal.ZERO)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public DreamAccountResponseDto.DreamAccountInfo getDreamAccountByUserId(String userId) {
		return dreamAccountRepository.findByUserId(userId)
			.map(dreamAccount -> {
				BigDecimal totalSafeBox = calculateTotalSafeBox(dreamAccount.getId());
				return DreamAccountResponseDto.DreamAccountInfo.from(dreamAccount, totalSafeBox);
			})
			.orElse(null);
	}

	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo createDreamAccount(DreamAccountRequestDto.Create requestDto,
		User user) {
		DreamAccount dreamAccount = requestDto.toEntity(user);
		DreamAccount savedDreamAccount = dreamAccountRepository.save(dreamAccount);

		BigDecimal totalSafeBox = calculateTotalSafeBox(savedDreamAccount.getId());
		return DreamAccountResponseDto.DreamAccountInfo.from(savedDreamAccount, totalSafeBox);
	}

	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo addAmountToDreamAccount(Long dreamAccountId, BigDecimal amount) {
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "꿈통장을 찾을 수 없습니다."));

		dreamAccount.setBalance(dreamAccount.getBalance().add(amount));
		dreamAccount.setTotal(dreamAccount.getTotal().add(amount));
		dreamAccountRepository.save(dreamAccount);

		BigDecimal totalSafeBox = calculateTotalSafeBox(dreamAccountId);
		return DreamAccountResponseDto.DreamAccountInfo.from(dreamAccount, totalSafeBox);
	}

	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo subtractAmountFromDreamAccount(Long dreamAccountId,
		BigDecimal amount) {
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "꿈통장을 찾을 수 없습니다."));

		if (dreamAccount.getBalance().compareTo(amount) < 0) {
			throw new CustomException(ErrorCode.TRANSFER_FAILURE, "꿈통장에 잔액이 부족합니다.");
		}

		dreamAccount.setBalance(dreamAccount.getBalance().subtract(amount));
		dreamAccount.setTotal(dreamAccount.getTotal().subtract(amount));
		dreamAccountRepository.save(dreamAccount);

		BigDecimal totalSafeBox = calculateTotalSafeBox(dreamAccountId);
		return DreamAccountResponseDto.DreamAccountInfo.from(dreamAccount, totalSafeBox);
	}

	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo distributeAmountToBucket(Long dreamAccountId, Long bucketId,
		BigDecimal amount) {
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "꿈통장을 찾을 수 없습니다."));

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

		if (dreamAccount.getBalance().compareTo(amount) < 0) {
			throw new CustomException(ErrorCode.TRANSFER_FAILURE, "꿈통장에 잔액이 부족합니다.");
		}

		dreamAccount.setBalance(dreamAccount.getBalance().subtract(amount));
		bucket.setSafeBox(bucket.getSafeBox().add(amount));

		dreamAccountRepository.save(dreamAccount);
		bucketRepository.save(bucket);

		BigDecimal totalSafeBox = calculateTotalSafeBox(dreamAccountId);
		return DreamAccountResponseDto.DreamAccountInfo.from(dreamAccount, totalSafeBox);
	}

	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo distributeAmountToDreamAccount(Long dreamAccountId, Long bucketId,
		BigDecimal amount) {
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "꿈통장을 찾을 수 없습니다."));

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "버킷리스트를 찾을 수 없습니다."));

		if (bucket.getSafeBox().compareTo(amount) < 0) {
			throw new CustomException(ErrorCode.TRANSFER_FAILURE, "버킷리스트의 SafeBox 잔액이 부족합니다.");
		}

		bucket.setSafeBox(bucket.getSafeBox().subtract(amount));
		dreamAccount.setBalance(dreamAccount.getBalance().add(amount));

		dreamAccountRepository.save(dreamAccount);
		bucketRepository.save(bucket);

		BigDecimal totalSafeBox = calculateTotalSafeBox(dreamAccountId);
		return DreamAccountResponseDto.DreamAccountInfo.from(dreamAccount, totalSafeBox);
	}
}
