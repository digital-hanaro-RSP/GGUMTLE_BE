package com.hana4.ggumtle.security.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.global.error.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Authentication Entry Point Handler
 * 사용자가 인증되지 않았거나 유효한 인증정보를 가지고 있지 않은 경우 동작
 * 로그인을 하지 않은 사용자가 로그인이 필요한 리소스에 접근할 때 동작한다. 해당 코드에선 401 관련 내용을 응답
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		log.info("[CustomAuthenticationEntryPointHandler] :: {}", authException.getMessage());
		log.info("[CustomAuthenticationEntryPointHandler] :: {}", request.getRequestURL());
		log.info("[CustomAuthenticationEntryPointHandler] :: 토근 정보가 만료되었거나 존재하지 않음");

		response.setStatus(ErrorCode.ACCESS_DENIED.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		Map<String, Object> body = new HashMap<>();
		body.put("code", ErrorCode.ACCESS_DENIED.getHttpStatus().value());
		body.put("error", ErrorCode.ACCESS_DENIED.getHttpStatus().getReasonPhrase());
		body.put("message", ErrorCode.ACCESS_DENIED.getMessage());

		objectMapper.writeValue(response.getWriter(), body);
	}
}
