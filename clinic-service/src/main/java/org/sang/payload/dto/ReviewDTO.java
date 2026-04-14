package org.sang.payload.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReviewDTO {
	private Long id;

	private Long property;

	private Long reviewer;

	private String reviewText;

	private Integer rating;

	private LocalDateTime createdAt;
}
