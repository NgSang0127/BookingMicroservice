package org.sang.service;

import java.util.List;
import org.sang.model.Notification;
import org.sang.payload.dto.NotificationDTO;

public interface NotificationService {
	NotificationDTO createNotification(Notification notification);
	List<Notification> getAllNotificationsByUserId(Long userId);
	List<Notification> getAllNotificationsByClinicId(Long clinicId);
	Notification markNotificationAsRead(Long notificationId) throws Exception;
	void deleteNotification(Long notificationId);
	List<Notification> getAllNotifications();
}
