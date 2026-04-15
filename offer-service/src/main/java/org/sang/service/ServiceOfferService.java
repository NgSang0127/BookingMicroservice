package org.sang.service;

import java.util.Set;
import org.sang.model.ServiceOffer;
import org.sang.payload.dto.CategoryDTO;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceDTO;

public interface ServiceOfferService {
	ServiceOffer createService(
			ServiceDTO service,
			ClinicDTO clinic,
			CategoryDTO category
	);

	ServiceOffer updateService(
			Long serviceId,
			ServiceOffer service
	) throws Exception;

	Set<ServiceOffer> getAllServicesByClinicId(Long clinicId,Long categoryId);

	ServiceOffer getServiceById(Long serviceId);

	Set<ServiceOffer> getServicesByIds(Set<Long> ids);
}
