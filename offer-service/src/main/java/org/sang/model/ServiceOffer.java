package org.sang.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ServiceOffer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int duration;

	private Long clinicId;

	@Column(nullable = false)
	private boolean available;

	@Column(nullable = false)
	private Long categoryId;


	private String image;
}
