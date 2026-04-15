package org.sang.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sang.model.ServiceOffer;
import org.sang.payload.dto.CategoryDTO;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceDTO;
import org.sang.repository.ServiceOfferRepository;
import org.sang.service.ServiceOfferService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceOfferServiceImpl implements ServiceOfferService {
	private final ServiceOfferRepository serviceOfferRepository;

	@Override
	public ServiceOffer createService(ServiceDTO service, ClinicDTO clinic, CategoryDTO category) {
		ServiceOffer serviceOffering=new ServiceOffer();
		serviceOffering.setName(service.getName());
		serviceOffering.setDescription(service.getDescription());
		serviceOffering.setPrice(service.getPrice());
		serviceOffering.setDuration(service.getDuration());
		serviceOffering.setImage(service.getImage());
		serviceOffering.setClinicId(clinic.getId());
		serviceOffering.setCategoryId(category.getId());
		return serviceOfferRepository.save(serviceOffering);
	}

	@Override
	public ServiceOffer updateService(Long serviceId, ServiceOffer service) throws Exception {
		Optional<ServiceOffer> existingService = serviceOfferRepository.findById(serviceId);
		if (existingService.isPresent()) {
			ServiceOffer updatedService = existingService.get();
			updatedService.setName(service.getName());
			updatedService.setDescription(service.getDescription());
			updatedService.setPrice(service.getPrice());
			updatedService.setDuration(service.getDuration());
			if(service.getImage()!=null){
				updatedService.setImage(service.getImage());
			}

			return serviceOfferRepository.save(updatedService);
		} else {
			throw new Exception("Service does not found");
		}
	}

	@Override
	public Set<ServiceOffer> getAllServicesByClinicId(Long clinicId, Long categoryId) {
		Set<ServiceOffer> services = serviceOfferRepository.findByClinicId(clinicId);
		if(categoryId != null) {
			services=services.stream()
					.filter(service -> service.getCategoryId() != null && service.getCategoryId().equals(categoryId))
					.collect(Collectors.toSet());
		}

		return services;
	}

	@Override
	public ServiceOffer getServiceById(Long serviceId) {
		Optional<ServiceOffer> service = serviceOfferRepository
				.findById(serviceId);
		return service.orElse(null);
	}

	@Override
	public Set<ServiceOffer> getServicesByIds(Set<Long> ids) {
		List<ServiceOffer> services = serviceOfferRepository
				.findAllById(ids);
		return new HashSet<>(services);
	}
}
