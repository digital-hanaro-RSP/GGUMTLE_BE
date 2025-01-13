package com.hana4.ggumtle.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final HttpStatus error;
    private final int code;
    private final String message;

    public static ResponseEntity<ErrorResponse> response(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ErrorResponse.builder().code(errorCode.getHttpStatus().value()).error(errorCode.getHttpStatus()).message(errorCode.getMessage()).build());
    }
}
