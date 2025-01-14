package com.hana4.ggumtle.security.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.global.error.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Access Denied Handler
 *  사용자가 접근한 리소스에 대한 권한이 없는 경우 동작하는 클래스
 * 403 관련 내용을 응답
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws
		IOException, ServletException {
		log.info("[CustomAccessDeniedHandler] :: {}", accessDeniedException.getMessage());
		log.info("[CustomAccessDeniedHandler] :: {}", request.getRequestURL());
		log.info("[CustomAccessDeniedHandler] :: 토큰 정보가 만료되었거나 존재하지 않음");

		response.setStatus(ErrorCode.FORBIDDEN.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		Map<String, Object> body = new HashMap<>();
		body.put("code", ErrorCode.FORBIDDEN.getHttpStatus().value());
		body.put("error", ErrorCode.FORBIDDEN.getHttpStatus().getReasonPhrase());
		body.put("message", ErrorCode.FORBIDDEN.getMessage());

		objectMapper.writeValue(response.getWriter(), body);
	}
}
