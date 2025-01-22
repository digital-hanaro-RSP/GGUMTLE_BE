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
	TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "Refresh token이 존재하지 않습니다."),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었거나 정상적인 Token이 아닙니다."),
	ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
	NOT_CORRECT(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 일치하지 않습니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "유효하지 않은 파라미터입니다."),
	SMS_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "SMS 처리에 실패했습니다. 잠시 후 다시 시도해주세요."),
	SMS_VALIDATION_FAILURE(HttpStatus.UNAUTHORIZED, "인증번호가 일치하지 않습니다."),
	SMS_ALREADY_SENT(HttpStatus.BAD_REQUEST, "인증 코드가 이미 발송되었습니다. 잠시 후 다시 시도해주세요."),
	REDIS_CONNECTION_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "Redis 연결에 실패했습니다."),
	DAILY_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "일일 SMS 인증 요청 한도를 초과했습니다. 내일 다시 시도해주세요."),
	TRANSFER_FAILURE(HttpStatus.BAD_REQUEST, "송금에 실패헸습니다."),
	FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 용량이 너무 큽니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
