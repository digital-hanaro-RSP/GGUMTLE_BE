package com.hana4.ggumtle.global.error;

import org.springframework.http.ResponseEntity;

import com.hana4.ggumtle.dto.CustomApiResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

	public static ResponseEntity<CustomApiResponse<Void>> response(ErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(CustomApiResponse.failure(
				errorCode.getHttpStatus().value(),
				errorCode.getHttpStatus().getReasonPhrase(),
				message
			));
	}
}
