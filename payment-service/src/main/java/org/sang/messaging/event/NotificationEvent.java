package org.sang.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
	private Long bookingId;
	private Long userId;
	private Long clinicId;
	private String type;
	private String description;
}
