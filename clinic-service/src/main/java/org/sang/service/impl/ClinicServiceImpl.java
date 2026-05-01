package org.sang.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.model.Clinic;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.repository.ClinicRepository;
import org.sang.service.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {
	private final ClinicRepository clinicRepository;

	@Override
	public Clinic createClinic(ClinicDTO req, UserDTO user) {
		Clinic clinic=new Clinic();
		clinic.setName(req.getName());
		clinic.setImages(req.getImages());
		clinic.setCity(req.getCity());
		clinic.setAddress(req.getAddress());
		clinic.setEmail(req.getEmail());
		clinic.setPhoneNumber(req.getPhoneNumber());
		clinic.setOpenTime(req.getOpenTime());
		clinic.setCloseTime(req.getCloseTime());
		clinic.setHomeService(true);
		clinic.setOpen(true);
		clinic.setOwnerId(user.getId());
		clinic.setActive(true);

		return clinicRepository.save(clinic);
	}

	@Override
	public Clinic updateClinic(Long clinicId, Clinic clinic) throws Exception {
		Clinic existingClinic = getClinicById(clinicId);
		if (existingClinic!=null) {

			existingClinic.setName(clinic.getName());
			existingClinic.setAddress(clinic.getAddress());
			existingClinic.setImages(clinic.getImages());
			existingClinic.setPhoneNumber(clinic.getPhoneNumber());
			existingClinic.setEmail(clinic.getEmail());
			existingClinic.setCity(clinic.getCity());
			existingClinic.setOpen(clinic.isOpen());
			existingClinic.setHomeService(clinic.isHomeService());
			existingClinic.setActive(clinic.isActive());
			existingClinic.setOpenTime(clinic.getOpenTime());
			existingClinic.setCloseTime(clinic.getCloseTime());

			return clinicRepository.save(existingClinic);
		}
		throw new Exception("Clinic does not exist");
	}

	@Override
	public Page<Clinic> getAllClinics(int page, int size) {
		Pageable pageable= PageRequest.of(page,size);
		return clinicRepository.findAll(pageable);
	}

	@Override
	public Clinic getClinicById(Long clinicId) throws Exception {
		return clinicRepository.findById(clinicId).orElseThrow(
				()-> new Exception("Clinic does not exist")
		);
	}

	@Override
	public Clinic getClinicByOwnerId(Long ownerId) {
		return clinicRepository.findByOwnerId(ownerId);
	}

	@Override
	public List<Clinic> searchClinicByCity(String city) {
		return clinicRepository.searchClinics(city);
	}
}
