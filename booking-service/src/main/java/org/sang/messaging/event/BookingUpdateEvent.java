package org.sang.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingUpdateEvent {
	private Long bookingId;
	private Long userId;
	private Long clinicId;
	private String paymentMethod;
	private String status; // "SUCCESS" hoặc "FAILED"
}
