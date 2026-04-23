package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.client.feign.BookingFeignClient;
import org.sang.mapper.NotificationMapper;
import org.sang.model.Notification;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.NotificationDTO;
import org.sang.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/notifications/clinic-owner")
@RequiredArgsConstructor
public class ClinicNotificationController {

	private final NotificationService notificationService;
	private final NotificationMapper notificationMapper;
	private final BookingFeignClient bookingFeignClient;

	@GetMapping("/clinic/{clinicId}")
	public ResponseEntity<List<NotificationDTO>> getNotificationsByClinicId(
			@PathVariable Long clinicId) {
		List<Notification> notifications = notificationService
				.getAllNotificationsByClinicId(clinicId);
		List<NotificationDTO> notificationDTOS=notifications
				.stream()
				.map((notification)-> {
					BookingDTO bookingDTO= bookingFeignClient
							.getBookingById(notification.getBookingId()).getBody();
					return notificationMapper.toDTO(notification,bookingDTO);
				}).collect(Collectors.toList());
		return ResponseEntity.ok(notificationDTOS);
	}
}
