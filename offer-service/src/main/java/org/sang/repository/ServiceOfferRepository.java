package org.sang.repository;

import java.util.Set;
import org.sang.model.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOfferRepository extends JpaRepository<ServiceOffer,Long> {
	Set<ServiceOffer> findByClinicId(Long id);
}
