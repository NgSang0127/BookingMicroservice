package org.sang.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.client.feign.BookingFeignClient;
import org.sang.mapper.NotificationMapper;
import org.sang.model.Notification;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.NotificationDTO;
import org.sang.repository.NotificationRepository;
import org.sang.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final BookingFeignClient bookingFeignClient;
	private final RealTimeCommunicationService realTimeCommunicationService;



	@Override
	public NotificationDTO createNotification(Notification notification) {
		Notification savedNotification= notificationRepository.save(notification);
		BookingDTO bookingDTO= bookingFeignClient
				.getBookingById(savedNotification.getBookingId()).getBody();

		NotificationDTO notificationDTO= notificationMapper.toDTO(
				savedNotification,
				bookingDTO
		);
		realTimeCommunicationService.sendNotification(notificationDTO);
		return notificationDTO;
	}

	@Override
	public List<Notification> getAllNotificationsByUserId(Long userId) {
		return notificationRepository.findByUserId(userId);
	}

	@Override
	public List<Notification> getAllNotificationsByClinicId(Long clinicId) {
		return notificationRepository.findByClinicId(clinicId);
	}

	@Override
	public Notification markNotificationAsRead(Long notificationId) throws Exception {
		return notificationRepository.findById(notificationId).map(notification -> {
			notification.setIsRead(true);
			return notificationRepository.save(notification);
		}).orElseThrow(() -> new Exception("Notification not found"));
	}

	@Override
	public void deleteNotification(Long notificationId) {
		if (notificationRepository.existsById(notificationId)) {
			notificationRepository.deleteById(notificationId);
		} else {
			throw new RuntimeException("Notification not found");
		}
	}

	@Override
	public List<Notification> getAllNotifications() {
		return notificationRepository.findAll();
	}
}
