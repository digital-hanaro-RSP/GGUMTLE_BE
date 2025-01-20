package com.hana4.ggumtle.repository;

import java.time.Duration;

import org.springframework.data.redis.RedisConnectionFailureException;
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

	private final RedisTemplate<String, String> redisTemplate;

	public void createSmsCertification(String phone, String certificationNumber) {
		try {
			redisTemplate.opsForValue()
				.set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
			log.info("SMS certification created for phone: {}", phone);
		} catch (RedisConnectionFailureException e) {
			log.error("Failed to connect to Redis", e);
			throw new RuntimeException("Redis connection failed", e);
		} catch (Exception e) {
			log.error("Error creating SMS certification", e);
			throw new RuntimeException("Error creating SMS certification", e);
		}
	}

	public String getSmsCertification(String phone) {
		try {
			String value = redisTemplate.opsForValue().get(PREFIX + phone);
			log.info("Retrieved SMS certification for phone: {}", phone);
			return value;
		} catch (Exception e) {
			log.error("Error retrieving SMS certification", e);
			return null;
		}
	}

	public void removeSmsCertification(String phone) {
		try {
			redisTemplate.delete(PREFIX + phone);
			log.info("Removed SMS certification for phone: {}", phone);
		} catch (Exception e) {
			log.error("Error removing SMS certification", e);
		}
	}
}
