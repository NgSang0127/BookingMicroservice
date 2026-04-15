package org.sang.controller;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sang.model.ServiceOffer;
import org.sang.service.ServiceOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/service-offering")
@RestController
@RequiredArgsConstructor
public class ServiceOfferController {
	private final ServiceOfferService serviceOfferingService;


	@GetMapping("/clinic/{clinicId}")
	public ResponseEntity<Set<ServiceOffer>> getServicesByClinicId(
			@PathVariable Long clinicId,
			@RequestParam(required = false) Long categoryId) {
		Set<ServiceOffer> services =  serviceOfferingService
				.getAllServicesByClinicId(clinicId,categoryId);

		return ResponseEntity.ok(services);

	}


	@GetMapping("/{serviceId}")
	public ResponseEntity<ServiceOffer> getServiceById(@PathVariable Long serviceId) throws Exception {
		ServiceOffer service = serviceOfferingService
				.getServiceById(serviceId);
		if (service == null) {
			throw new Exception("Service not found with id "+serviceId);
		}
		return ResponseEntity.ok(service);

	}

	@GetMapping("/list/{ids}")
	public ResponseEntity<Set<ServiceOffer>> getServicesByIds(
			@PathVariable Set<Long> ids) {
		Set<ServiceOffer> services =  serviceOfferingService
				.getServicesByIds(ids);

		return ResponseEntity.ok(services);


	}
}
