package org.sang.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.sang.constant.BookingStatus;
import org.sang.model.Booking;
import org.sang.model.ClinicReport;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceOfferingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.BookingRequest;

public interface BookingService {
	Booking createBooking(
			BookingRequest booking,
			UserDTO user,
			ClinicDTO clinicDTO,
			Set<ServiceOfferingDTO> serviceOfferingSet) throws Exception;


	List<Booking> getBookingsByCustomer(Long customerId);


	List<Booking> getBookingsByClinic(Long clinicId);


	Booking getBookingById(Long bookingId);

	Booking bookingSuccess(PaymentOrder order);


	Booking updateBookingStatus(Long bookingId, BookingStatus status) throws Exception;

	ClinicReport getClinicReport(Long clinicId);

	List<Booking> getBookingsByDate(LocalDate date,Long clinicId);
}
