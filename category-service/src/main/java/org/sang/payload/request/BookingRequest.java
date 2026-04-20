package org.sang.payload.request;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

@Data
public class BookingRequest {

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Set<Long> serviceIds;

	private String status;

}
