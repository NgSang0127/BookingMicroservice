package org.sang.payload.dto;

import lombok.Data;

@Data
public class ServiceDTO {
	private String name;

	private String description;


	private int price;


	private int duration;


	private Long category;

	private String image;
}
