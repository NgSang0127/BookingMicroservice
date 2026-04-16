package org.sang.messaging;

import lombok.RequiredArgsConstructor;
import org.sang.model.PaymentOrder;
import org.sang.service.BookingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {
	private final BookingService bookingService;

	@RabbitListener(queues = "booking-queue")
	public void bookingUpdateListener(PaymentOrder paymentOrder){

		System.out.println("Received message: " + paymentOrder);

		bookingService.bookingSuccess(paymentOrder);

		System.out.println("Received message: " + paymentOrder);

	}
}
