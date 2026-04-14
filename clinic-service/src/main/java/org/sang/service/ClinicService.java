package org.sang.service;

import java.util.List;
import org.sang.model.Clinic;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface ClinicService {

	Clinic createClinic(ClinicDTO clinic, UserDTO user);

	Clinic updateClinic(Long clinicId, Clinic clinic) throws Exception;

	Page<Clinic> getAllClinics(int page, int size);

	Clinic getClinicById(Long clinicId) throws Exception;

	Clinic getClinicByOwnerId(Long ownerId);

	List<Clinic> searchClinicByCity(String city);

}
