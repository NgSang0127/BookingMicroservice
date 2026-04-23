package org.sang.service.impl;

import lombok.RequiredArgsConstructor;
import org.sang.payload.dto.NotificationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;


@Controller
@RequiredArgsConstructor
public class RealTimeCommunicationService {


	private final SimpMessagingTemplate simpMessagingTemplate;



	public void sendNotification(NotificationDTO notification) {
		simpMessagingTemplate.convertAndSend(
				"/notification/user/"+notification.getUserId(),
				notification
		);
		simpMessagingTemplate.convertAndSend(
				"/notification/clinic/"+notification.getClinicId(),
				notification
		);
	}



}
