package com.hana4.ggumtle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 응답에 포함되지 않음
public class CustomApiResponse<T> {

	private final int code;
	private final String error;
	private final String message;
	private final T data;

	// 기본 생성자를 막아 직접 인스턴스화 방지
	private CustomApiResponse() {
		throw new IllegalStateException("Cannot instantiate ApiResponse directly");
	}

	/**
	 * 성공 응답 생성 (데이터 없을 때)
	 */
	public static <T> CustomApiResponse<T> success() {
		return new CustomApiResponse<>(200, null, "ok", null);
	}

	/**
	 * 성공 응답 생성 (데이터 있을 때)
	 */
	public static <T> CustomApiResponse<T> success(T data) {
		return new CustomApiResponse<>(200, null, "ok", data);
	}

	/**
	 * 실패 응답 생성
	 */
	public static <T> CustomApiResponse<T> failure(int code, String error, String message) {
		return new CustomApiResponse<>(code, error, message, null);
	}
}
