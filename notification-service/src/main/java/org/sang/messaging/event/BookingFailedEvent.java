package org.sang.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingFailedEvent {

	private Long bookingId;
	private Long paymentOrderId;
	private String reason;
}
