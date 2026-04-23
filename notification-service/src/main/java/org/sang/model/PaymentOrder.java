package org.sang.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sang.constant.PaymentMethod;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentOrder {

	private Long id;

	private Long amount;

	private PaymentMethod paymentMethod;

	private String paymentLinkId;

	private Long userId;

	private Long bookingId;

	private Long clinicId;
}
