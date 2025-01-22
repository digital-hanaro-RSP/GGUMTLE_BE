package com.hana4.ggumtle.repository;

import java.time.Duration;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis를 이용한 RefreshToken 관리
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class RefreshTokenRepository {

	private static final String PREFIX = "refresh_token:";
	private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60; // 7일

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * Refresh token 저장
	 *
	 * @param refreshToken refresh token
	 * @param userId 사용자 ID
	 */
	public void saveRefreshToken(String refreshToken, String userId) {
		redisTemplate.opsForValue()
			.set(PREFIX + refreshToken, userId, Duration.ofSeconds(REFRESH_TOKEN_EXPIRY));
		log.info("Refresh token 저장: {} -> 사용자 ID: {}", refreshToken, userId);
	}

	/**
	 * Refresh token 조회
	 *
	 * @param refreshToken refresh token
	 * @return 사용자 ID
	 */
	public String getRefreshToken(String refreshToken) {
		String userId = redisTemplate.opsForValue().get(PREFIX + refreshToken);
		if (userId == null) {
			log.warn("존재하지 않는 Refresh token: {}", refreshToken);
			throw new CustomException(ErrorCode.TOKEN_NOT_EXIST);
		}
		log.info("Refresh token 조회: {} -> 사용자 ID: {}", refreshToken, userId);
		return userId;
	}

	/**
	 * Refresh token 삭제
	 *
	 * @param refreshToken refresh token
	 */
	public void deleteRefreshToken(String refreshToken) {
		if (Boolean.TRUE.equals(redisTemplate.delete(PREFIX + refreshToken))) {
			log.info("Refresh token 삭제: {}", refreshToken);
		} else {
			log.warn("삭제하려는 Refresh token이 존재하지 않음: {}", refreshToken);
		}
	}

	/**
	 * 사용자 ID에 해당하는 모든 Refresh token 삭제
	 *
	 * @param userId 사용자 ID
	 */
	public void deleteAllTokensByUserId(String userId) {
		Set<String> keys = redisTemplate.keys(PREFIX + "*");
		if (keys != null) {
			keys.stream()
				.filter(key -> userId.equals(redisTemplate.opsForValue().get(key)))
				.forEach(key -> {
					if (Boolean.TRUE.equals(redisTemplate.delete(key))) {
						log.info("Refresh token 삭제: {} (사용자 ID: {})", key, userId);
					} else {
						log.warn("삭제 실패한 Refresh token: {} (사용자 ID: {})", key, userId);
					}
				});
			log.info("사용자 ID: {}에 해당하는 모든 Refresh token 삭제 완료", userId);
		} else {
			log.info("삭제할 Refresh token이 없음 (사용자 ID: {})", userId);
		}
	}
}
