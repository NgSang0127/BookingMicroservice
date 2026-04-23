package org.sang.payload.dto;

import lombok.Data;

@Data
public class ServiceOfferDTO {

	private Long id;

	private String name;

	private String description;

	private int price;

	private int duration;

	private Long clinic;

	private boolean available;

	private Long category;

	private String image;
}
