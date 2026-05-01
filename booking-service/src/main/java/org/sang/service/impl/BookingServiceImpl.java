package org.sang.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sang.constant.BookingStatus;
import org.sang.model.Booking;
import org.sang.model.ClinicReport;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceOfferingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.BookingRequest;
import org.sang.repository.BookingRepository;
import org.sang.service.BookingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepository;

	@Override
	public Booking createBooking(BookingRequest booking,
			UserDTO user,
			ClinicDTO clinic,
			Set<ServiceOfferingDTO> serviceOfferingSet
	) throws Exception {

		int totalDuration = serviceOfferingSet.stream()
				.mapToInt(ServiceOfferingDTO::getDuration)
				.sum();

		LocalDateTime bookingStartTime = booking.getStartTime();
		LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);

//        check availability

		Boolean isSlotAvailable = isTimeSlotAvailable(clinic, bookingStartTime, bookingEndTime);

		if (!isSlotAvailable) {
			throw new Exception("Slot is not available");
		}

		int totalPrice = serviceOfferingSet.stream()
				.mapToInt(ServiceOfferingDTO::getPrice)
				.sum();

		Set<Long> idList = serviceOfferingSet.stream()
				.map(ServiceOfferingDTO::getId)
				.collect(Collectors.toSet());

		Booking newBooking = new Booking();
		newBooking.setCustomerId(user.getId());
		newBooking.setClinicId(clinic.getId());
		newBooking.setStartTime(bookingStartTime);
		newBooking.setEndTime(bookingEndTime);
		newBooking.setServiceIds(idList);
		newBooking.setTotalPrice(totalPrice);
		newBooking.setStatus(BookingStatus.PENDING);

		return bookingRepository.save(newBooking);
	}

	public Boolean isTimeSlotAvailable(ClinicDTO clinic,
			LocalDateTime bookingStartTime,
			LocalDateTime bookingEndTime) throws Exception {

		List<Booking> existingBookings = getBookingsByClinic(clinic.getId())
				.stream()
				.filter(b -> b.getStatus() != BookingStatus.CANCELLED)
				.toList();

		LocalDateTime clinicOpenTime = clinic.getOpenTime().atDate(bookingStartTime.toLocalDate());
		LocalDateTime clinicCloseTime = clinic.getCloseTime().atDate(bookingStartTime.toLocalDate());

		if (bookingStartTime.isBefore(clinicOpenTime) || bookingEndTime.isAfter(clinicCloseTime)) {
			throw new Exception("Booking time must be within clinic's open hours.");
		}

		for (Booking existingBooking : existingBookings) {
			LocalDateTime existingStart = existingBooking.getStartTime();
			LocalDateTime existingEnd = existingBooking.getEndTime();

			if (bookingStartTime.isBefore(existingEnd) && bookingEndTime.isAfter(existingStart)) {
				throw new Exception("Slot not available, choose different time.");
			}
		}

		return true;
	}

	@Override
	public List<Booking> getBookingsByCustomer(Long customerId) {
		return bookingRepository.findByCustomerId(customerId);
	}

	@Override
	public List<Booking> getBookingsByClinic(Long clinicId) {
		return bookingRepository.findByClinicId(clinicId);
	}

	@Override
	public Booking getBookingById(Long bookingId) {
		Optional<Booking> booking = bookingRepository.findById(bookingId);
		return booking.orElse(null);
	}

	@Override
	public Booking bookingSuccess(PaymentOrder order) {
		Booking existingBooking = getBookingById(order.getBookingId());
		existingBooking.setStatus(BookingStatus.CONFIRMED);
		return bookingRepository.save(existingBooking);
	}

	@Override
	public Booking updateBookingStatus(Long bookingId,
			BookingStatus status) throws Exception {
		Booking existingBooking = getBookingById(bookingId);
		if (existingBooking == null) {
			throw new Exception("booking not found");
		}

		existingBooking.setStatus(status);
		return bookingRepository.save(existingBooking);

	}

	@Override
	public ClinicReport getClinicReport(Long clinicId) {

		List<Booking> bookings = getBookingsByClinic(clinicId);

		ClinicReport report = new ClinicReport();

		// Total Earnings: Sum of totalPrice for all bookings
		Double totalEarnings = bookings.stream()
				.mapToDouble(Booking::getTotalPrice)
				.sum();

		// Total Bookings: Count of all bookings
		Integer totalBookings = bookings.size();

		// Cancelled Bookings: Filter bookings with status CANCELLED
		List<Booking> cancelledBookings = bookings.stream()
				.filter(booking -> booking.getStatus().toString().equalsIgnoreCase("CANCELLED"))
				.collect(Collectors.toList());

		// Refunds: Calculate based on cancelled bookings (same totalPrice as refunded)
		Double totalRefund = cancelledBookings.stream()
				.mapToDouble(Booking::getTotalPrice)
				.sum();

		report.setTotalEarnings(totalEarnings);
		report.setTotalBookings(totalBookings);
		report.setCancelledBookings(cancelledBookings.size());
		report.setTotalRefund(totalRefund);

		return report;

	}

	@Override
	public List<Booking> getBookingsByDate(LocalDate date, Long clinicId) {
		List<Booking> allBookings = bookingRepository.findByClinicId(clinicId);

		if (date == null) {
			return allBookings;
		}

		return allBookings.stream()
				.filter(booking -> isSameDate(booking.getStartTime(), date) ||
						isSameDate(booking.getEndTime(), date))
				.collect(Collectors.toList());
	}

	private boolean isSameDate(LocalDateTime dateTime, LocalDate date) {
		return dateTime.toLocalDate().isEqual(date);
	}
}
