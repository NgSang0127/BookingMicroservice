package org.sang.client.feign;

import org.sang.payload.dto.ClinicDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("CLINIC-SERVICE")
public interface ClinicFeignClient {
	@GetMapping("/api/clinics/owner")
	public ResponseEntity<ClinicDTO> getClinicByOwner(
			@RequestHeader("Authorization")String jwt) throws Exception;
}
