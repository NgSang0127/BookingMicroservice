package org.sang.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.messaging.event.BookingUpdateEvent;
import org.sang.model.PaymentOrder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventProducer {

	private final KafkaTemplate<String, BookingUpdateEvent> kafkaTemplate;

	public void sentBookingUpdateEvent(PaymentOrder paymentOrder) {
		BookingUpdateEvent event = new BookingUpdateEvent(
				paymentOrder.getBookingId(),
				paymentOrder.getUserId(),
				paymentOrder.getClinicId(),
				paymentOrder.getPaymentMethod().name(),
				"SUCCESS"
		);
		kafkaTemplate.send("booking-update", String.valueOf(event.getBookingId()), event);
		log.info("[Kafka] → booking-update: bookingId={}", event.getBookingId());
	}
}
