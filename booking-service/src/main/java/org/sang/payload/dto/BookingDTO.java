package org.sang.payload.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import org.sang.constant.BookingStatus;

@Data
public class BookingDTO {

	private Long id;

	private Long clinicId;

	private ClinicDTO clinic;

	private Long customerId;

	private UserDTO customer;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Set<Long> servicesIds;

	private Set<ServiceOfferingDTO> services;

	private BookingStatus status;

	private int totalPrice;
}
