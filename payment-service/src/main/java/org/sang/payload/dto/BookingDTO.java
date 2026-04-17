package org.sang.payload.dto;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
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

	private Set<ServiceDTO> services;

	private BookingStatus status;

	private int totalPrice;
}
