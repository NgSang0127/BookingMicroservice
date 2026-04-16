package org.sang.mapper;

import java.util.Set;
import org.sang.model.Booking;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceOfferingDTO;
import org.sang.payload.dto.UserDTO;

public class BookingMapper {
	// Convert Booking entity to BookingDTO
	public static BookingDTO toDTO(Booking booking,
			Set<ServiceOfferingDTO> serviceOfferingDTOS,
			ClinicDTO clinicDTO,
			UserDTO userDTO
	) {
		if (booking == null) {
			return null;
		}

		BookingDTO bookingDTO = new BookingDTO();
		bookingDTO.setId(booking.getId());
		bookingDTO.setClinicId(booking.getClinicId());
		bookingDTO.setCustomerId(booking.getCustomerId());
		bookingDTO.setStartTime(booking.getStartTime());
		bookingDTO.setEndTime(booking.getEndTime());
		bookingDTO.setServicesIds(booking.getServiceIds());
		bookingDTO.setStatus(booking.getStatus());
		bookingDTO.setTotalPrice(booking.getTotalPrice());

		// If services mapping is needed (convert from serviceIds to ServiceOfferingDTOs)
		bookingDTO.setServices(serviceOfferingDTOS);
		bookingDTO.setCustomer(userDTO);
		bookingDTO.setClinic(clinicDTO);

		return bookingDTO;
	}

	// Convert BookingDTO to Booking entity
	public static Booking toEntity(BookingDTO bookingDTO) {
		if (bookingDTO == null) {
			return null;
		}

		Booking booking = new Booking();
		booking.setId(bookingDTO.getId());
		booking.setClinicId(bookingDTO.getClinicId());
		booking.setCustomerId(bookingDTO.getCustomerId());
		booking.setStartTime(bookingDTO.getStartTime());
		booking.setEndTime(bookingDTO.getEndTime());
		booking.setServiceIds(bookingDTO.getServicesIds());
		booking.setStatus(bookingDTO.getStatus());
		booking.setTotalPrice(bookingDTO.getTotalPrice());

		// Additional logic for mapping services can be added here if needed.

		return booking;
	}
}
