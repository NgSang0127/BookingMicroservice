package org.sang.service;

import java.util.List;
import org.sang.constant.ClinicStatus;
import org.sang.model.Clinic;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ClinicService {

	Clinic createClinic(ClinicDTO clinic, UserDTO user);

	Clinic updateClinic(Long clinicId, Clinic clinic) throws Exception;

	Page<Clinic> getAllClinics(int page, int size, ClinicStatus status);

	Clinic getClinicById(Long clinicId) throws Exception;

	Clinic getClinicByOwnerId(Long ownerId);

	List<Clinic> searchClinicByCity(String city);

	Clinic approveClinic(Long clinicId, ClinicStatus status, String reason) throws Exception;

	void deleteClinic(Long id);
}
