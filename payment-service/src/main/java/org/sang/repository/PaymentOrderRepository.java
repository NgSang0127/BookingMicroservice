package org.sang.repository;

import org.sang.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {

	PaymentOrder findByPaymentLinkId(String paymentId);
}