package com.hana4.ggumtle.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hana4.ggumtle.dto.CustomApiResponse;

@RestControllerAdvice
public class ErrorController {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<CustomApiResponse<Void>> exceptionHandler(CustomException e) {
		return ErrorResponse.response(e.getErrorCode(), e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CustomApiResponse.failure(400, "Bad Request",
				ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
	}
}
