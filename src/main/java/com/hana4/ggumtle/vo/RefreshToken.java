package com.hana4.ggumtle.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RefreshToken 저장 객체
 *
 * <p>
 * 해당 프로젝트는 스프링 시큐리티 위주의 프로젝트이기 때문에 간단하게 구현
 * 운영환경에서는 해당 방식이 아닌 Redis 사용을 추천
 * Redis 에서 만료시간을 설정하여 관리
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

	// Refresh token should be stored as String, not Long
	protected static final Map<String, String> refreshTokens = new HashMap<>();

	/**
	 * refresh token get
	 *
	 * @param refreshToken refresh token
	 * @return id
	 */
	public static String getRefreshToken(final String refreshToken) {
		return Optional.ofNullable(refreshTokens.get(refreshToken))
			.orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_EXIST));
	}

	/**
	 * refresh token put
	 *
	 * @param refreshToken refresh token
	 * @param id id
	 */
	public static void putRefreshToken(final String refreshToken, String id) {
		refreshTokens.put(refreshToken, id);
	}

	/**
	 * refresh token remove
	 *
	 * @param refreshToken refresh token
	 */
	private static void removeRefreshToken(final String refreshToken) {
		refreshTokens.remove(refreshToken);
	}

	// user refresh token remove
	public static void removeUserRefreshToken(final String refreshToken) {
		for (Map.Entry<String, String> entry : refreshTokens.entrySet()) {
			if (entry.getValue().equals(refreshToken)) {
				removeRefreshToken(entry.getKey());
			}
		}
	}
}
