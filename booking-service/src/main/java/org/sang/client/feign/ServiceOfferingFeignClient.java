package org.sang.client.feign;

import org.sang.payload.dto.ServiceOfferingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient("OFFER-SERVICE")
public interface ServiceOfferingFeignClient {

	@GetMapping("/api/service-offering/list/{ids}")
	public ResponseEntity<Set<ServiceOfferingDTO>> getServicesByIds(
			@PathVariable Set<Long> ids);
}
