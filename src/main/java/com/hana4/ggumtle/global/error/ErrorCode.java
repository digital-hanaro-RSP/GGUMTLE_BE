package com.hana4.ggumtle.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재합니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
