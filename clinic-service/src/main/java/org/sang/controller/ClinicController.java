package org.sang.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.client.feign.UserFeignClient;
import org.sang.constant.ClinicStatus;
import org.sang.exception.UserException;
import org.sang.mapper.ClinicMapper;
import org.sang.model.Clinic;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.ApproveClinicRequest;
import org.sang.service.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clinics")
public class ClinicController {

	private final ClinicService clinicService;
	private final UserFeignClient userService;

	@PostMapping
	public ResponseEntity<ClinicDTO> createClinic(
			@RequestBody ClinicDTO clinic) throws UserException {
		UserDTO user = userService.getUserProfile();

		Clinic createdClinic = clinicService.createClinic(clinic, user);

		ClinicDTO clinicDTO = ClinicMapper.mapToDTO(createdClinic, user);

		return new ResponseEntity<>(clinicDTO, HttpStatus.CREATED);
	}

	@PutMapping("/{clinicId}")
	public ResponseEntity<ClinicDTO> updateClinic(
			@PathVariable Long clinicId,
			@RequestBody Clinic clinic
	) throws Exception {
		Clinic updatedClinic = clinicService.updateClinic(clinicId, clinic);
		UserDTO user = userService.getUserById(updatedClinic.getOwnerId());

		ClinicDTO clinicDTO = ClinicMapper.mapToDTO(updatedClinic, user);

		return new ResponseEntity<>(clinicDTO, HttpStatus.OK);


	}

	@GetMapping
	public ResponseEntity<Page<ClinicDTO>> getAllClinics(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) ClinicStatus status
	) throws UserException {
		Page<Clinic> clinics = clinicService.getAllClinics(page, size,status);
		Page<ClinicDTO> clinicDTOPage = clinics.map(clinic -> {
			UserDTO owner = null;
			try {
				owner = userService
						.getUserById(clinic.getOwnerId());
			} catch (UserException e) {
				throw new RuntimeException(e);
			}

			return ClinicMapper.mapToDTO(clinic, owner);
		});

		return ResponseEntity.ok(clinicDTOPage);
	}

	@GetMapping("/{clinicId}")
	public ResponseEntity<ClinicDTO> getClinicById(@PathVariable Long clinicId) throws Exception {
		Clinic clinic = clinicService.getClinicById(clinicId);
		if (clinic == null) {
			throw new Exception("Clinic does not exist with id " + clinicId);
		}
		UserDTO user = userService.getUserById(clinic.getOwnerId());

		ClinicDTO clinicDTO = ClinicMapper.mapToDTO(clinic, user);

		return ResponseEntity.ok(clinicDTO);
	}

	@GetMapping("/search")
	public ResponseEntity<List<ClinicDTO>> searchClinic(
			@RequestParam("city") String city) throws Exception {
		List<Clinic> clinics = clinicService.searchClinicByCity(city);
		List<ClinicDTO> clinicDTOS = new ArrayList<>();
		for (Clinic clinic1 : clinics) {
			UserDTO owner = userService.getUserById(clinic1.getOwnerId());
			ClinicDTO apply = ClinicMapper.mapToDTO(clinic1, owner);
			clinicDTOS.add(apply);
		}
		return ResponseEntity.ok(clinicDTOS);
	}

	@GetMapping("/owner")
	public ResponseEntity<Clinic> getClinicByOwner() throws Exception {
		UserDTO user = userService.getUserProfile();
		System.out.println("Clinic " + user);
		Clinic clinic = clinicService.getClinicByOwnerId(user.getId());

		return ResponseEntity.ok(clinic);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteClinic(@PathVariable Long id){
		 clinicService.deleteClinic(id);
		 return ResponseEntity.ok("Xóa clinic thành công");
	}


	@PutMapping("/{clinicId}/approval")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ClinicDTO> approveClinic(
			@PathVariable Long clinicId,
			@RequestBody ApproveClinicRequest request
	) throws Exception {
		Clinic clinic = clinicService.approveClinic(
				clinicId,
				request.getStatus(),
				request.getReason()
		);
		UserDTO user = userService.getUserById(clinic.getOwnerId());
		return ResponseEntity.ok(ClinicMapper.mapToDTO(clinic, user));
	}
}
