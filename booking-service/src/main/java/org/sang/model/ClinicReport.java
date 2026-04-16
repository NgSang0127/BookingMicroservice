package org.sang.model;

import lombok.Data;

@Data
public class ClinicReport {
	private Long clinicId;
	private String clinicName;
	private Double totalEarnings;
	private Integer totalBookings;
	private Integer cancelledBookings;
	private Double totalRefund;
}
