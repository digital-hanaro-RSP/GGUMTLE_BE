package com.hana4.ggumtle.security.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtUtil {

	/**
	 * Spring Security Context에서 로그인한 사용자의 id 조회
	 *
	 * @param authentication Authentication
	 * @return 로그인한 사용자의 id
	 * @throws AccessDeniedException AccessDeniedException
	 */
	public String getLoginId(final Authentication authentication) throws AccessDeniedException {
		// 정상적으로 로그인한 사용자 정보인지 체크
		checkAuth(authentication);

		if (authentication.getPrincipal() instanceof UserDetails) {
			return ((UserDetails)authentication.getPrincipal()).getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			return (String)authentication.getPrincipal();
		}
		throw new IllegalArgumentException("Unexpected principal type");
	}

	/**
	 * 정상적으로 로그인한 사용자 정보인지 체크
	 *
	 * @param authentication Authentication
	 * @throws AccessDeniedException AccessDeniedException
	 */
	private void checkAuth(final Authentication authentication) throws AccessDeniedException {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new CustomException(ErrorCode.ACCESS_DENIED, "로그인 정보가 존재하지 않습니다.");
		}
	}

}
