package org.sang.payload.request;

import lombok.Data;
import org.sang.constant.ClinicStatus;

@Data
public class ApproveClinicRequest {
	private ClinicStatus status;
	private String reason;
}