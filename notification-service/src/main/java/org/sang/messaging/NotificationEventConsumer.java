package org.sang.messaging;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.messaging.event.NotificationEvent;
import org.sang.model.Notification;
import org.sang.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventConsumer {

	private final NotificationService notificationService;

	@KafkaListener(
			topics = {"notification-event", "notify-failed"},
			groupId = "notification-group"
	)
	public void consume(NotificationEvent event) {
		log.info("[Saga] ← notification event: type={}, bookingId={}",
				event.getType(), event.getBookingId());

		// Idempotency check – tránh tạo notification trùng
		boolean alreadyExists = notificationService
				.existsByBookingIdAndType(event.getBookingId(), event.getType());

		if (alreadyExists) {
			log.warn("[Saga] Notification already exists, skip: bookingId={}, type={}",
					event.getBookingId(), event.getType());
			return;
		}

		// Map event → entity đúng với các field trong model
		Notification notification = new Notification();
		notification.setBookingId(event.getBookingId());
		notification.setUserId(event.getUserId());
		notification.setClinicId(event.getClinicId());
		notification.setType(event.getType());
		notification.setDescription(event.getDescription()); // đúng tên field
		notification.setIsRead(false);
		notification.setCreatedAt(LocalDateTime.now());      // set thời điểm nhận event

		notificationService.createNotification(notification);
		log.info("[Saga] Notification saved: type={}, bookingId={}",
				event.getType(), event.getBookingId());
	}
}
