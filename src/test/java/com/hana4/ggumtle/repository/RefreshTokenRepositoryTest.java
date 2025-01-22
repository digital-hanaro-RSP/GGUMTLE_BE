package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class RefreshTokenRepositoryTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private RefreshTokenRepository refreshTokenRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	void saveRefreshToken_Success() {
		String refreshToken = "validRefreshToken";
		String userId = "userId";

		refreshTokenRepository.saveRefreshToken(refreshToken, userId);

		verify(valueOperations, times(1)).set(eq("refresh_token:" + refreshToken), eq(userId), any());
	}

	@Test
	void getRefreshToken_Success() {
		String refreshToken = "validRefreshToken";
		String userId = "userId";

		when(valueOperations.get("refresh_token:" + refreshToken)).thenReturn(userId);

		String result = refreshTokenRepository.getRefreshToken(refreshToken);

		assertThat(result).isEqualTo(userId);
		verify(valueOperations, times(1)).get("refresh_token:" + refreshToken);
	}

	@Test
	void getRefreshToken_TokenNotExist() {
		String refreshToken = "invalidRefreshToken";

		when(valueOperations.get("refresh_token:" + refreshToken)).thenReturn(null);

		assertThatThrownBy(() -> refreshTokenRepository.getRefreshToken(refreshToken))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.TOKEN_NOT_EXIST.getMessage());
		verify(valueOperations, times(1)).get("refresh_token:" + refreshToken);
	}

	@Test
	void deleteRefreshToken_Success() {
		String refreshToken = "validRefreshToken";

		when(redisTemplate.delete("refresh_token:" + refreshToken)).thenReturn(true);

		refreshTokenRepository.deleteRefreshToken(refreshToken);

		verify(redisTemplate, times(1)).delete("refresh_token:" + refreshToken);
	}

	@Test
	void deleteRefreshToken_TokenNotExist() {
		String refreshToken = "nonExistentRefreshToken";

		when(redisTemplate.delete("refresh_token:" + refreshToken)).thenReturn(false);

		refreshTokenRepository.deleteRefreshToken(refreshToken);

		verify(redisTemplate, times(1)).delete("refresh_token:" + refreshToken);
	}

	@Test
	void deleteAllTokensByUserId_Success() {
		String userId = "userId";
		Set<String> keys = Set.of("refresh_token:token1", "refresh_token:token2");

		when(redisTemplate.keys("refresh_token:*")).thenReturn(keys);
		when(valueOperations.get("refresh_token:token1")).thenReturn(userId);
		when(valueOperations.get("refresh_token:token2")).thenReturn(userId);
		when(redisTemplate.delete(anyString())).thenReturn(true);

		refreshTokenRepository.deleteAllTokensByUserId(userId);

		verify(redisTemplate, times(1)).keys("refresh_token:*");
		verify(valueOperations, times(2)).get(anyString());
		verify(redisTemplate, times(2)).delete(anyString());
	}

	@Test
	void deleteAllTokensByUserId_NoTokensToDelete() {
		String userId = "userId";

		when(redisTemplate.keys("refresh_token:*")).thenReturn(null);

		refreshTokenRepository.deleteAllTokensByUserId(userId);

		verify(redisTemplate, times(1)).keys("refresh_token:*");
		verify(valueOperations, never()).get(anyString());
		verify(redisTemplate, never()).delete(anyString());
	}

	@Test
	void deleteAllTokensByUserId_DeletionFails() {
		// Arrange
		String PREFIX = "refresh_token:";
		String userId = "testUser";
		String tokenKey = PREFIX + "123";
		Set<String> keys = new HashSet<>();
		keys.add(tokenKey);

		// Mock Redis behavior
		when(redisTemplate.keys(PREFIX + "*")).thenReturn(keys);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(tokenKey)).thenReturn(userId);
		when(redisTemplate.delete(tokenKey)).thenReturn(false); // Simulate deletion failure

		// Capture logs using Logback's ListAppender
		Logger logger = (Logger)LoggerFactory.getLogger(refreshTokenRepository.getClass());
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		logger.addAppender(listAppender);

		// Act
		refreshTokenRepository.deleteAllTokensByUserId(userId);

		// Assert
		verify(redisTemplate, times(1)).delete(tokenKey); // Ensure delete was called
		List<ILoggingEvent> logs = listAppender.list;
		assertThat(logs.stream().anyMatch(event ->
			event.getLevel().toString().equals("WARN") &&
				event.getFormattedMessage().contains("삭제 실패한 Refresh token: " + tokenKey) &&
				event.getFormattedMessage().contains("사용자 ID: " + userId)
		)).isTrue();

		// Clean up logger appender
		logger.detachAppender(listAppender);
	}
}
