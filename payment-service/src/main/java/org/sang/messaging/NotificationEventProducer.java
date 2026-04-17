package org.sang.messaging;

import lombok.RequiredArgsConstructor;
import org.sang.payload.dto.NotificationDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {
	private final RabbitTemplate rabbitTemplate;

	public void sentNotificationEvent(Long bookingId,
			Long userId,
			Long clinicId) {
		NotificationDTO notification=new NotificationDTO();
		notification.setBookingId(bookingId);
		notification.setClinicId(clinicId);
		notification.setUserId(userId);
		notification.setDescription("new booking got confirmed");
		notification.setType("BOOKING");

		rabbitTemplate.convertAndSend("notification-queue", notification);

	}
}
