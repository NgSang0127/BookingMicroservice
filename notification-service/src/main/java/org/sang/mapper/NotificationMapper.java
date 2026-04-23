package org.sang.mapper;

import org.sang.model.Notification;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.NotificationDTO;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {

	public NotificationDTO toDTO(Notification notification,
			BookingDTO bookingDTO) {
		if (notification == null) {
			return null;
		}

		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setId(notification.getId());
		notificationDTO.setType(notification.getType());
		notificationDTO.setIsRead(notification.getIsRead());
		notificationDTO.setDescription(notification.getDescription());
		notificationDTO.setUserId(notification.getUserId());
		notificationDTO.setBookingId(notification.getBookingId());
		notificationDTO.setClinicId(notification.getClinicId());



		notificationDTO.setBooking(bookingDTO);

		return notificationDTO;
	}

}
