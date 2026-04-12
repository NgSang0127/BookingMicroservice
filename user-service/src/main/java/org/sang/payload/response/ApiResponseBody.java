package org.sang.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.sang.exception.BusinessErrorCodes;

public class ApiResponseBody <T> {

	private boolean success;
	private String message;

	@JsonIgnore
	private BusinessErrorCodes businessErrorCodes;
	private String timestamp;
	private final T data;

	public ApiResponseBody(boolean success, String message, BusinessErrorCodes businessErrorCodes, T data) {
		this.success = success;
		this.message = message;
		this.businessErrorCodes = businessErrorCodes;
		this.timestamp = LocalDateTime.now().toString();
		this.data = data;
	}

	public ApiResponseBody(boolean success, String message, T data, T data1) {
		this.success = success;
		this.message = message;
		this.data = data1;
		this.timestamp = ZonedDateTime.now(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

	}
}
