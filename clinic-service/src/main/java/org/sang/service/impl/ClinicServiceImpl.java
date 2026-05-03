package org.sang.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.constant.ClinicStatus;
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
		Clinic existing = clinicRepository.findByOwnerId(user.getId());
		if (existing != null) {
			throw new RuntimeException("Bạn đã đăng ký clinic, vui lòng chờ duyệt hoặc liên hệ admin.");
		}

		Clinic clinic = new Clinic();
		clinic.setName(req.getName());
		clinic.setImages(req.getImages());
		clinic.setCity(req.getCity());
		clinic.setAddress(req.getAddress());
		clinic.setEmail(req.getEmail());
		clinic.setPhoneNumber(req.getPhoneNumber());
		clinic.setOpenTime(req.getOpenTime());
		clinic.setCloseTime(req.getCloseTime());
		clinic.setHomeService(req.isHomeService());
		clinic.setOpen(false);
		clinic.setOwnerId(user.getId());
		clinic.setStatus(ClinicStatus.PENDING);

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
			existingClinic.setOpenTime(clinic.getOpenTime());
			existingClinic.setCloseTime(clinic.getCloseTime());

			return clinicRepository.save(existingClinic);
		}
		throw new Exception("Clinic does not exist");
	}

	@Override
	public Page<Clinic> getAllClinics(int page, int size, ClinicStatus status) {
		Pageable pageable= PageRequest.of(page,size);
		if (status != null) {
			return clinicRepository.findByStatus(status, pageable);
		}
		return clinicRepository.findAll(pageable);
	}

	@Override
	public Clinic getClinicById(Long clinicId) throws Exception {
		return clinicRepository.findById(clinicId).orElseThrow(
				()-> new Exception("Clinic does not exist")
		);
	}

	@Override
	public void deleteClinic(Long id) {
		clinicRepository.deleteById(id);
	}

	@Override
	public Clinic getClinicByOwnerId(Long ownerId) {
		return clinicRepository.findByOwnerId(ownerId);
	}

	@Override
	public List<Clinic> searchClinicByCity(String city) {
		return clinicRepository.searchClinics(city,ClinicStatus.APPROVED);
	}
	@Override
	public Clinic approveClinic(Long clinicId, ClinicStatus status, String reason) throws Exception {
		Clinic clinic = getClinicById(clinicId);

		if (status == ClinicStatus.APPROVED) {
			clinic.setStatus(ClinicStatus.APPROVED);
			clinic.setOpen(true);
			clinic.setRejectedReason(null);
		} else if (status == ClinicStatus.REJECTED) {
			clinic.setStatus(ClinicStatus.REJECTED);
			clinic.setOpen(false);
			clinic.setRejectedReason(reason);
		} else if (status == ClinicStatus.SUSPENDED) {
			clinic.setStatus(ClinicStatus.SUSPENDED);
			clinic.setOpen(false);
			clinic.setRejectedReason(reason);
		} else {
			throw new Exception("Trạng thái không hợp lệ");
		}

		return clinicRepository.save(clinic);
	}
}
