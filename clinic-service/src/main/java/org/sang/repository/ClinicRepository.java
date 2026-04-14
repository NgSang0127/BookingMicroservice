package org.sang.repository;

import java.util.List;
import org.sang.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

	Clinic findByOwnerId(Long ownerId);

	@Query("SELECT s FROM Clinic s WHERE " +
			"(LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(s.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
			"s.active = true")
	List<Clinic> searchClinics(@Param("keyword") String keyword);
}
