package org.sang.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.client.feign.PaymentFeignClient;
import org.sang.client.feign.ServiceOfferingFeignClient;
import org.sang.client.feign.UserFeignClient;
import org.sang.constant.BookingStatus;
import org.sang.constant.PaymentMethod;
import org.sang.exception.UserException;
import org.sang.mapper.BookingMapper;
import org.sang.model.Booking;
import org.sang.model.ClinicReport;
import org.sang.payload.dto.BookedSlotsDTO;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceOfferingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.BookingRequest;
import org.sang.payload.response.PaymentLinkResponse;
import org.sang.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
@RequestMapping("/api/bookings")
public class BookingController {

	private final BookingService bookingService;
	private final UserFeignClient userService;
	private final ClinicFeignClient clinicService;
	private final ServiceOfferingFeignClient serviceOfferingService;
	private final PaymentFeignClient paymentService;
	private final UserFeignClient userFeignClient;

	@PostMapping
	public ResponseEntity<PaymentLinkResponse> createBooking(
			@RequestParam Long clinicId, @RequestParam PaymentMethod paymentMethod,
			@RequestBody BookingRequest bookingRequest) throws Exception {

		UserDTO user = userService.getUserProfile();

		ClinicDTO clinic = clinicService.getClinicById(clinicId).getBody();

		if (clinic.getId() == null) {
			throw new Exception("Clinic does not found");
		}

		Set<ServiceOfferingDTO> services = serviceOfferingService.getServicesByIds(bookingRequest.getServiceIds())
				.getBody();

		Booking createdBooking = bookingService.createBooking(bookingRequest, user, clinic, services);
		PaymentLinkResponse res = paymentService.createPaymentLink(createdBooking, paymentMethod).getBody();

		return new ResponseEntity<>(res, HttpStatus.CREATED);

	}

	/**
	 * Get all bookings for a customer
	 */
	@GetMapping("/customer")
	public ResponseEntity<Set<BookingDTO>> getBookingsByCustomer()
			throws UserException {

		UserDTO user = userService.getUserProfile();

		List<Booking> bookings = bookingService.getBookingsByCustomer(user.getId());

		return ResponseEntity.ok(getBookingDTOs(bookings));

	}

	/**
	 * Get all bookings for a clinic
	 */
	@GetMapping("/report")
	public ResponseEntity<ClinicReport> getClinicReport() throws Exception {

		UserDTO user = userService.getUserProfile();

		ClinicDTO clinic = clinicService.getClinicByOwner().getBody();

		ClinicReport report = bookingService.getClinicReport(clinic.getId());

		return ResponseEntity.ok(report);

	}

	@GetMapping("/clinic")
	public ResponseEntity<Set<BookingDTO>> getBookingsByClinic() throws Exception {

		UserDTO user = userService.getUserProfile();

		ClinicDTO clinic = clinicService.getClinicByOwner().getBody();

		List<Booking> bookings = bookingService.getBookingsByClinic(clinic.getId());

		return ResponseEntity.ok(getBookingDTOs(bookings));


	}

	private Set<BookingDTO> getBookingDTOs(List<Booking> bookings) {

		return bookings.stream().map(booking -> {
			UserDTO user;
			Set<ServiceOfferingDTO> offeringDTOS = serviceOfferingService.getServicesByIds(booking.getServiceIds())
					.getBody();

			ClinicDTO clinicDTO;
			try {
				clinicDTO = clinicService.getClinicById(booking.getClinicId()).getBody();
				user = userFeignClient.getUserById(booking.getCustomerId());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return BookingMapper.toDTO(booking, offeringDTOS, clinicDTO, user);
		}).collect(Collectors.toSet());
	}

	/**
	 * Get a booking by its ID
	 */
	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long bookingId) {
		Booking booking = bookingService.getBookingById(bookingId);
		Set<ServiceOfferingDTO> offeringDTOS = serviceOfferingService.getServicesByIds(booking.getServiceIds())
				.getBody();

		BookingDTO bookingDTO = BookingMapper.toDTO(booking, offeringDTOS, null, null);

		return ResponseEntity.ok(bookingDTO);


	}

	/**
	 * Update the status of a booking
	 */
	@PutMapping("/{bookingId}/status")
	public ResponseEntity<BookingDTO> updateBookingStatus(@PathVariable Long bookingId,
			@RequestParam BookingStatus status) throws Exception {

		Booking updatedBooking = bookingService.updateBookingStatus(bookingId, status);

		Set<ServiceOfferingDTO> offeringDTOS = serviceOfferingService.getServicesByIds(updatedBooking.getServiceIds())
				.getBody();

		ClinicDTO clinicDTO;
		try {
			clinicDTO = clinicService.getClinicById(updatedBooking.getClinicId()).getBody();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		BookingDTO bookingDTO = BookingMapper.toDTO(updatedBooking, offeringDTOS, clinicDTO, null);

		return new ResponseEntity<>(bookingDTO, HttpStatus.OK);

	}

	@GetMapping("/slots/clinic/{clinicId}/date/{date}")
	public ResponseEntity<List<BookedSlotsDTO>> getBookedSlots(@PathVariable Long clinicId,
			@PathVariable LocalDate date) throws Exception {

		List<Booking> bookings = bookingService.getBookingsByDate(date, clinicId);

		List<BookedSlotsDTO> slotsDTOS = bookings.stream().map(booking -> {
			BookedSlotsDTO slotDto = new BookedSlotsDTO();

			slotDto.setStartTime(booking.getStartTime());
			slotDto.setEndTime(booking.getEndTime());

			return slotDto;
		}).toList();

		return ResponseEntity.ok(slotsDTOS);


	}
}
