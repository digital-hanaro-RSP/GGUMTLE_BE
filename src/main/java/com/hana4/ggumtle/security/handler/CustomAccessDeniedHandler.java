package com.hana4.ggumtle.security.handler;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.hana4.ggumtle.global.error.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Access Denied Handler
 *  사용자가 접근한 리소스에 대한 권한이 없는 경우 동작하는 클래스
 * 403 관련 내용을 응답
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws
		IOException, ServletException {
		log.info("[CustomAccessDeniedHandler] :: {}", accessDeniedException.getMessage());
		log.info("[CustomAccessDeniedHandler] :: {}", request.getRequestURL());
		log.info("[CustomAccessDeniedHandler] :: 토큰 정보가 만료되었거나 존재하지 않음");

		response.setStatus(ErrorCode.FORBIDDEN.getHttpStatus().value());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");

		JsonObject returnJson = new JsonObject();
		returnJson.addProperty("code", ErrorCode.FORBIDDEN.getHttpStatus().value());
		returnJson.addProperty("error", ErrorCode.FORBIDDEN.getHttpStatus().getReasonPhrase());
		returnJson.addProperty("message", ErrorCode.FORBIDDEN.getMessage());

		PrintWriter out = response.getWriter();
		out.print(returnJson);
	}
}
