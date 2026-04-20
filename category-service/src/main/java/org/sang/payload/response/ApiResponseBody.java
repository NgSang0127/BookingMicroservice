package org.sang.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.sang.constant.BusinessCodeErrors;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseBody<T> {
	private boolean success;
	private String message;

	@JsonIgnore
	private BusinessCodeErrors businessCodeErrors;
	private String timestamp;
	private final T data;

	public ApiResponseBody(boolean success, String message, BusinessCodeErrors businessCodeErrors, T data) {
		this.success = success;
		this.message = message;
		this.businessCodeErrors = businessCodeErrors;
		this.timestamp = LocalDateTime.now().toString();
		this.data = data;
	}

	public ApiResponseBody(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.timestamp = ZonedDateTime.now(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
		this.data = data;
	}
}