package org.sang.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.model.Booking;
import org.sang.payload.dto.ClinicDTO;
import org.sang.service.BookingService;
import org.sang.service.impl.BookingChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings/chart")
public class ChartController {

	private final BookingChartService bookingChartService;
	private final BookingService bookingService;
	private final ClinicFeignClient clinicService;

	@GetMapping("/earnings")
	public ResponseEntity<List<Map<String, Object>>> getEarningsChartData(
			@RequestHeader("Authorization") String jwt) throws Exception {

//        UserDTO user = userService.getUserFromJwtToken(jwt).getBody();

		ClinicDTO clinic = clinicService.getClinicByOwner(jwt).getBody();
		List<Booking> bookings=bookingService.getBookingsByClinic(clinic.getId());

		// Generate chart data
		List<Map<String, Object>> chartData = bookingChartService
				.generateEarningsChartData(bookings);

		return ResponseEntity.ok(chartData);

	}

	@GetMapping("/bookings")
	public ResponseEntity<List<Map<String, Object>>> getBookingsChartData(
			@RequestHeader("Authorization") String jwt) throws Exception {

		ClinicDTO clinic = clinicService.getClinicByOwner(jwt).getBody();
		List<Booking> bookings=bookingService.getBookingsByClinic(clinic.getId());
		// Generate chart data
		List<Map<String, Object>> chartData = bookingChartService.generateBookingCountChartData(bookings);

		return ResponseEntity.ok(chartData);

	}
}
