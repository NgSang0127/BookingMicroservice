package org.sang.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.constant.BookingStatus;
import org.sang.messaging.event.BookingFailedEvent;
import org.sang.messaging.event.BookingUpdateEvent;
import org.sang.model.Booking;
import org.sang.service.BookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingUpdateConsumer {

	private final BookingService bookingService;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@KafkaListener(topics = "booking-update", groupId = "booking-group")
	public void consume(BookingUpdateEvent event) {
		log.info("[Saga] ← booking-update: bookingId={}", event.getBookingId());

		try {
			// Idempotency check
			Booking booking = bookingService.getBookingById(event.getBookingId());
			if (BookingStatus.CONFIRMED.equals(booking.getStatus())) {
				log.warn("[Saga] Booking already CONFIRMED, skip: {}", event.getBookingId());
				return;
			}

			bookingService.updateBookingStatus(event.getBookingId(), BookingStatus.CONFIRMED);
			log.info("[Saga] Booking CONFIRMED: {}", event.getBookingId());

		} catch (Exception e) {
			log.error("[Saga] Failed to update booking: {}", e.getMessage());

			// COMPENSATION: gửi sự kiện thất bại để payment rollback
			BookingFailedEvent failedEvent = new BookingFailedEvent(
					event.getBookingId(),
					null, // paymentOrderId nếu có trong event
					e.getMessage()
			);
			kafkaTemplate.send("booking-failed",
					String.valueOf(event.getBookingId()), failedEvent);

			log.warn("[Saga] → booking-failed sent: bookingId={}", event.getBookingId());
		}
	}
}
