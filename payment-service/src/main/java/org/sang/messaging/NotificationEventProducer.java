package org.sang.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.messaging.event.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sentNotificationEvent(Long bookingId, Long userId, Long clinicId) {
		NotificationEvent event = new NotificationEvent(
				bookingId,
				userId,
				clinicId,
				"SUCCESS",
				"Đặt lịch thành công, vui lòng đến đúng giờ"
		);
		kafkaTemplate.send("notification-event", String.valueOf(bookingId), event);
		log.info("[Kafka] → notification-event SUCCESS: bookingId={}", bookingId);
	}

	public void sentFailedNotificationEvent(Long bookingId, Long userId, Long clinicId) {
		NotificationEvent event = new NotificationEvent(
				bookingId,
				userId,
				clinicId,
				"FAILED",
				"Đặt lịch thất bại, tiền sẽ được hoàn trong 3-5 ngày"
		);
		kafkaTemplate.send("notify-failed", String.valueOf(bookingId), event);
		log.info("[Kafka] → notify-failed: bookingId={}", bookingId);
	}
}
