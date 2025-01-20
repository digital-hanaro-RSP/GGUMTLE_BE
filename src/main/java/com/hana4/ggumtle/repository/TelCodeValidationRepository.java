package com.hana4.ggumtle.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class TelCodeValidationRepository {
	private static final String PREFIX = "sms:";
	private static final int LIMIT_TIME = 3 * 60;

	private static final String DAILY_LIMIT_PREFIX = "daily_sms_limit:";
	private static final int MAX_DAILY_REQUESTS = 10;
	private static final int DAILY_LIMIT_EXPIRY = 24 * 60 * 60; // 24시간

	private final RedisTemplate<String, String> redisTemplate;

	public void createSmsCertification(String phone, String certificationNumber) {
		redisTemplate.opsForValue()
			.set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
		log.info("전화번호 {}에 대한 SMS 인증 생성 완료", phone);
	}

	// 일일 요청 횟수 증가 및 확인
	public boolean incrementDailyRequestCount(String phone) {
		String dailyLimitKey = DAILY_LIMIT_PREFIX + phone;
		Long dailyCount = redisTemplate.opsForValue().increment(dailyLimitKey);

		// 첫 요청인 경우 만료 시간 설정
		if (dailyCount == 1) {
			redisTemplate.expire(dailyLimitKey, Duration.ofSeconds(DAILY_LIMIT_EXPIRY));
		}

		log.info("전화번호 {}의 일일 요청 횟수: {}", phone, dailyCount);

		// 일일 최대 요청 횟수를 초과하지 않았는지 확인
		return dailyCount <= MAX_DAILY_REQUESTS;
	}

	public String getSmsCertification(String phone) {
		String value = redisTemplate.opsForValue().get(PREFIX + phone);
		log.info("전화번호 {}에 대한 SMS 인증 조회 완료", phone);
		return value;
	}

	public void removeSmsCertification(String phone) {
		redisTemplate.delete(PREFIX + phone);
		log.info("전화번호 {}에 대한 SMS 인증 삭제 완료", phone);
	}

	public boolean hasKey(String phone) {
		boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + phone));
		log.info("전화번호 {}에 대한 키 존재 여부 확인: {}", phone, exists ? "존재함" : "존재하지 않음");
		return exists;
	}
}
