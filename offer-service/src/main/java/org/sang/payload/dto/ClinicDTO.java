package org.sang.payload.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class ClinicDTO {
	private Long id;
	private String name;
	private String address;
	private String phoneNumber;
	private String email;
	private String city;
	private boolean isOpen;
	private boolean homeService;
	private boolean active;
	private Long ownerId;
	private LocalTime openTime;
	private LocalTime closeTime;
	private List<String> images;
}
