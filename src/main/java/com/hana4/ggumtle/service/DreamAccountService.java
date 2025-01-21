package com.hana4.ggumtle.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.dreamAccount.DreamAccountRequestDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.BucketRepository;
import com.hana4.ggumtle.repository.DreamAccountRepository;
import com.hana4.ggumtle.repository.UserRepository;

@Service
public class DreamAccountService {

	@Autowired
	private DreamAccountRepository dreamAccountRepository;

	@Autowired
	private BucketRepository bucketRepository;

	@Autowired
	private UserRepository userRepository;

	public DreamAccountService(DreamAccountRepository dreamAccountRepository) {
		this.dreamAccountRepository = dreamAccountRepository;
	}

	public DreamAccount getDreamAccountByUserId(String userId) {
		return dreamAccountRepository.findByUserId(userId)
			.orElse(null); // DreamAccount가 없으면 null 반환
	}

	@Transactional
	public DreamAccount createDreamAccount(DreamAccountRequestDto.Create requestDto, User user) {
		// User 확인
		// User user = userRepository.findById(requestDto.getUserId())
		// 	.orElseThrow(() -> new RuntimeException("User not found"));

		// // 기존 꿈통장이 존재하면 예외 처리
		// if (dreamAccountRepository.existsByUser(user)) {
		// 	throw new RuntimeException("Dream Account already exists for this user");
		// }

		System.out.println("User: " + user);
		// 새로운 DreamAccount 생성
		DreamAccount newDreamAccount = requestDto.toEntity(user);

		// 저장 및 반환
		return dreamAccountRepository.save(newDreamAccount);
	}

	// 꿈통장에 금액 추가
	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo addAmountToDreamAccount(Long dreamAccountId, BigDecimal amount) {

		// DreamAccount 조회
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new RuntimeException("Dream Account not found"));

		// DreamAccount 금액 업데이트
		dreamAccount.setBalance(dreamAccount.getBalance().add(amount));
		dreamAccount.setTotal(dreamAccount.getTotal().add(amount));
		dreamAccountRepository.save(dreamAccount);

		// 전체 Bucket의 safeBox 합계 계산
		BigDecimal totalSafeBox = bucketRepository.findAllByDreamAccountId(dreamAccountId).stream()
			.map(Bucket::getSafeBox)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		// DreamAccountInfo 반환
		return DreamAccountResponseDto.DreamAccountInfo.fromEntity(dreamAccount, totalSafeBox);
	}

	// 꿈통장에서 금액 제외
	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo subtractAmountFromDreamAccount(Long dreamAccountId,
		BigDecimal amount) {
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new RuntimeException("Dream Account not found"));
		// 금액 제외
		if (dreamAccount.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance in Dream Account");
		}

		dreamAccount.setBalance(dreamAccount.getBalance().subtract(amount));
		dreamAccount.setTotal(dreamAccount.getTotal().subtract(amount));  // Total 금액에서 제외
		dreamAccountRepository.save(dreamAccount);
		BigDecimal totalSafeBox = bucketRepository.findAllByDreamAccountId(dreamAccountId).stream()
			.map(Bucket::getSafeBox)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		return DreamAccountResponseDto.DreamAccountInfo.fromEntity(dreamAccount, totalSafeBox);
	}

	// 꿈통장 금액을 Bucket의 safeBox로 분배
	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo distributeAmountToBucket(Long dreamAccountId, Long bucketId,
		BigDecimal amount) {
		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new RuntimeException("Dream Account not found"));

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new RuntimeException("Bucket not found"));

		// 꿈통장에 금액이 충분한지 확인
		if (dreamAccount.getBalance().compareTo(amount) < 0) {
			throw new RuntimeException("Insufficient balance in Dream Account");
		}

		// 금액을 꿈통장에서 제외하고 Bucket의 safeBox에 추가
		dreamAccount.setBalance(dreamAccount.getBalance().subtract(amount));
		bucket.setSafeBox(bucket.getSafeBox().add(amount));

		// 업데이트 저장
		dreamAccountRepository.save(dreamAccount);
		bucketRepository.save(bucket);

		BigDecimal totalSafeBox = bucketRepository.getTotalSafeBoxByDreamAccountId(dreamAccountId);

		// 7. DreamAccountInfo DTO 생성 및 반환
		return DreamAccountResponseDto.DreamAccountInfo.fromEntity(dreamAccount, totalSafeBox);
	}

	@Transactional
	public DreamAccountResponseDto.DreamAccountInfo distributeAmountToDreamAccount(Long dreamAccountId, Long bucketId,
		BigDecimal amount) {

		DreamAccount dreamAccount = dreamAccountRepository.findById(dreamAccountId)
			.orElseThrow(() -> new RuntimeException("Dream Account not found"));

		Bucket bucket = bucketRepository.findById(bucketId)
			.orElseThrow(() -> new RuntimeException("Bucket not found"));

		// 꿈통장에 금액이 충분한지 확인
		if (bucket.getSafeBox().compareTo(amount) < 0) {
			throw new RuntimeException("세이프박스에 돈이 모자랍니다.");
		}
		dreamAccount.setBalance(dreamAccount.getBalance().add(amount));
		bucket.setSafeBox(bucket.getSafeBox().subtract(amount));

		dreamAccountRepository.save(dreamAccount);
		bucketRepository.save(bucket);
		BigDecimal totalSafeBox = bucketRepository.getTotalSafeBoxByDreamAccountId(dreamAccountId);

		// 7. DreamAccountInfo DTO 생성 및 반환
		return DreamAccountResponseDto.DreamAccountInfo.fromEntity(dreamAccount, totalSafeBox);

	}
}
