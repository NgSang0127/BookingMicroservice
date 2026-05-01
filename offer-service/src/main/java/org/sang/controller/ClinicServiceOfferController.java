package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.client.feign.CategoryFeignClient;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.model.ServiceOffer;
import org.sang.payload.dto.CategoryDTO;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceDTO;
import org.sang.service.ServiceOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-offering/clinic-owner")
public class ClinicServiceOfferController {

	private final ServiceOfferService serviceOfferingService;
	private final ClinicFeignClient clinicService;
	private final CategoryFeignClient categoryService;

	@PostMapping
	public ResponseEntity<ServiceOffer> createService(
			@RequestBody ServiceDTO service) throws Exception {

		ClinicDTO clinic=clinicService.getClinicByOwner().getBody();

		CategoryDTO category=categoryService
				.getCategoryById(service.getCategory()).getBody();

		ServiceOffer createdService = serviceOfferingService
				.createService(service,clinic,category);
		return new ResponseEntity<>(createdService, HttpStatus.CREATED);
	}

	@PatchMapping("/{serviceId}")
	public ResponseEntity<ServiceOffer> updateService(
			@PathVariable Long serviceId,
			@RequestBody ServiceOffer service) throws Exception {
		ServiceOffer updatedService = serviceOfferingService
				.updateService(serviceId, service);
		if (updatedService != null) {
			return new ResponseEntity<>(updatedService, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
