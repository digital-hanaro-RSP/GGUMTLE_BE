package com.hana4.ggumtle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "API 응답")
@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 응답에 포함되지 않음
public class CustomApiResponse<T> {
	@Schema(description = "응답 코드", example = "200")
	private final int code;
	// @Schema(description = "에러 메시지", example = "에러일때만 나옴 ex) Bad Request, Not Found")
	@Schema(hidden = true)
	private final String error;
	@Schema(description = "응답 메시지", example = "ok")
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
