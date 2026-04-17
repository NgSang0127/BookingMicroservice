package org.sang.payload.dto;

import lombok.Data;

@Data
public class NotificationDTO {

	private Long id;
	private String type;
	private Boolean isRead = false;
	private String description;
	private Long userId;
	private Long bookingId;
	private Long clinicId;
	private BookingDTO booking;
}
