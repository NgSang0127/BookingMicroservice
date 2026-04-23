package org.sang.repository;

import java.util.List;
import org.sang.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,
		Long> {
	List<Notification> findByUserId(Long userId);
	List<Notification> findByClinicId(Long clinicId);
}
