package org.sang.repository;

import java.util.List;
import org.sang.constant.ClinicStatus;
import org.sang.model.Clinic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

	Clinic findByOwnerId(Long ownerId);

	Page<Clinic> findByStatus(ClinicStatus status, Pageable pageable);

	@Query("SELECT c FROM Clinic c WHERE " +
			"(LOWER(c.city)    LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			" LOWER(c.name)    LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			" LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
			"c.status = :status AND " +
			"c.isOpen = true")
	List<Clinic> searchClinics(
			@Param("keyword") String keyword,
			@Param("status") ClinicStatus status
	);
}
