package com.hana4.ggumtle.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hana4.ggumtle.dto.ApiResponse;

@RestControllerAdvice
public class ErrorController {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> exceptionHandler(CustomException e) {
		return ErrorResponse.response(e.getErrorCode(), e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.failure(400, "Bad Request",
				ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
	}
}
