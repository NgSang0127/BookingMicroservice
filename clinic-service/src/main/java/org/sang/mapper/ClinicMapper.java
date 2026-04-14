package org.sang.mapper;

import org.sang.model.Clinic;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class ClinicMapper {

	public static ClinicDTO mapToDTO(Clinic clinic, UserDTO userDTO) {
		if (clinic == null) {
			return null;
		}

		ClinicDTO clinicDTO = new ClinicDTO();
		clinicDTO.setId(clinic.getId());
		clinicDTO.setName(clinic.getName());
		clinicDTO.setAddress(clinic.getAddress());
		clinicDTO.setPhoneNumber(clinic.getPhoneNumber());
		clinicDTO.setEmail(clinic.getEmail());
		clinicDTO.setCity(clinic.getCity());
//        clinicDTO.setIsOpen(clinic.isOpen());
		clinicDTO.setHomeService(clinic.isHomeService());
		clinicDTO.setActive(clinic.isActive());
		clinicDTO.setOwnerId(clinic.getOwnerId());
		clinicDTO.setOpenTime(clinic.getOpenTime());
		clinicDTO.setCloseTime(clinic.getCloseTime());
		clinicDTO.setImages(clinic.getImages());
		clinicDTO.setOwner(userDTO);

		return clinicDTO;
	}
}
