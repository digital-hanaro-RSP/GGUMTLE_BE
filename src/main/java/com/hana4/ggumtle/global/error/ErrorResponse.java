package com.hana4.ggumtle.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hana4.ggumtle.dto.ApiResponse;

@Getter
@Builder
public class ErrorResponse {

    public static ResponseEntity<ApiResponse<Void>> response(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(ApiResponse.failure(
                errorCode.getHttpStatus().value(),
                errorCode.getHttpStatus().getReasonPhrase(),
                message
            ));
    }
}
