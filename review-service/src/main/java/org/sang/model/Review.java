package org.sang.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Entity
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String reviewText;

	@Column(nullable = false)
	private double rating;

	@Column(nullable = false)
	private Long clinicId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	@CreatedDate
	private LocalDateTime createdAt=LocalDateTime.now();
}
