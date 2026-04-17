package org.sang.payload.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {

	private Long id;

	private Long property;

	private Long reviewer;

	private String reviewText;

	private Integer rating;

	private LocalDateTime createdAt;
}
