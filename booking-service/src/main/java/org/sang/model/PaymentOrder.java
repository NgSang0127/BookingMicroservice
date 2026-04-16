package org.sang.model;

import lombok.Data;
import org.sang.constant.PaymentMethod;
import org.sang.constant.PaymentOrderStatus;

@Data
public class PaymentOrder {
	private Long id;

	private Long amount;

	private PaymentOrderStatus status;

	private PaymentMethod paymentMethod;

	private String paymentLinkId;

	private Long userId;

	private Long bookingId;
}
