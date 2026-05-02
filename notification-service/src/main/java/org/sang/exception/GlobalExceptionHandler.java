package org.sang.exception;

import org.sang.payload.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(feign.FeignException.class)
	public ResponseEntity<ExceptionResponse> handleFeignException(feign.FeignException ex, WebRequest req) {
		int status = ex.status();
		if (status <= 0) status = 500;

		ExceptionResponse response = new ExceptionResponse(
				"Lỗi từ Service liên kết: " + ex.contentUTF8(),
				req.getDescription(false),
				LocalDateTime.now()
		);

		return ResponseEntity.status(status).body(response);
	}



	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> ExceptionHandler(Exception ex, WebRequest req) {
		ExceptionResponse response = new ExceptionResponse(
				ex.getMessage(),
				req.getDescription(false), LocalDateTime.now());
//		response.setMessage(ex.getMessage());
		return ResponseEntity.ok(response);
	}

}
