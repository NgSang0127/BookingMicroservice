package org.sang.payload.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingRequest {

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Set<Long> serviceIds;

	private String status;

}
