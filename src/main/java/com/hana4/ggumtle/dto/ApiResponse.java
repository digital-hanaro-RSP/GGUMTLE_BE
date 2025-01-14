package com.hana4.ggumtle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 응답에 포함되지 않음
public class ApiResponse<T> {

	private final int code;
	private final String error;
	private final String message;
	private final T data;

	// 기본 생성자를 막아 직접 인스턴스화 방지
	private ApiResponse() {
		throw new IllegalStateException("Cannot instantiate ApiResponse directly");
	}

	/**
	 * 성공 응답 생성 (데이터 없을 때)
	 */
	public static <T> ApiResponse<T> success() {
		return new ApiResponse<>(200, null, "ok", null);
	}

	/**
	 * 성공 응답 생성 (데이터 있을 때)
	 */
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(200, null, "ok", data);
	}

	/**
	 * 실패 응답 생성
	 */
	public static <T> ApiResponse<T> failure(int code, String error, String message) {
		return new ApiResponse<>(code, error, message, null);
	}
}
