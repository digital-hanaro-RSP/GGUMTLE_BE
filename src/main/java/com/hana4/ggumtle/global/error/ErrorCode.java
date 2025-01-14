package com.hana4.ggumtle.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재합니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류"),
	;

	private final HttpStatus httpStatus;
	private final String message;
}
