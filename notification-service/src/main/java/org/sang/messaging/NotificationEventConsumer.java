package org.sang.messaging;

import lombok.RequiredArgsConstructor;
import org.sang.model.Notification;
import org.sang.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

	private final NotificationService notificationService;

	@RabbitListener(queues = "notification-queue")
	public void sentBookingUpdateEvent(Notification notification){
		notificationService.createNotification(notification);
	}

}
