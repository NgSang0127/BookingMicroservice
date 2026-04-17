package org.sang.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sang.constant.PaymentMethod;
import org.sang.constant.PaymentOrderStatus;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long amount;

	private PaymentOrderStatus status = PaymentOrderStatus.PENDING;

	private PaymentMethod paymentMethod;

	private String paymentLinkId;


	private Long userId;

	private Long bookingId;

	@Column(nullable = false)
	private Long clinicId;
}
