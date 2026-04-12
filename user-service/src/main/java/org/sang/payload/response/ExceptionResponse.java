package org.sang.payload.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {
	private String message;
	private String error;
	private LocalDateTime timestamp;

}
