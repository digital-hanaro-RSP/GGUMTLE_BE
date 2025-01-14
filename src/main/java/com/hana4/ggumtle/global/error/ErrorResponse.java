package com.hana4.ggumtle.global.error;

import org.springframework.http.ResponseEntity;

import com.hana4.ggumtle.dto.ApiResponse;

import lombok.Builder;
import lombok.Getter;

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
