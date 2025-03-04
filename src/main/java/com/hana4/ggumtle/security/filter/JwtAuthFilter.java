package com.hana4.ggumtle.security.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.security.provider.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// 요청을 필터링하고 JWT를 사용하여 인증 및 권한 부여를 처리
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		final String token = request.getHeader("Authorization");

		// Bearer token 검증 후 user name 조회
		if (token != null && token.startsWith("Bearer ")) {
			String jwtToken = token.substring(7);
			String username = jwtProvider.getUsernameFromToken(jwtToken);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				try {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				} catch (UsernameNotFoundException e) {
					// 인증되지 않은 사용자에 대한 예외 처리
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패: " + e.getMessage());
					return; // 필터 체인을 중단
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * token의 사용자 idx를 이용하여 사용자 정보 조회하고, UsernamePasswordAuthenticationToken 생성
	 */
	private UsernamePasswordAuthenticationToken getUserAuth(User user) {
		return new UsernamePasswordAuthenticationToken(user.getTel(),
			user.getPassword(),
			Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
		);
	}

}
