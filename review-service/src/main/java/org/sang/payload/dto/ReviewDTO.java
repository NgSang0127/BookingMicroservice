package org.sang.payload.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReviewDTO {

	private Long id;

	private Long property;

	private UserDTO user;

	private ClinicDTO clinic;

	private String reviewText;

	private double rating;

	private LocalDateTime createdAt;
}
