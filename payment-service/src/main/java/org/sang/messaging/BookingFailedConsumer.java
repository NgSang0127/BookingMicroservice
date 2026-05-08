package org.sang.messaging;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.constant.PaymentOrderStatus;
import org.sang.messaging.event.BookingFailedEvent;
import org.sang.model.PaymentOrder;
import org.sang.repository.PaymentOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingFailedConsumer {

	@Value("${stripe.secret-key}")
	private String stripeSecretKey;

	@Value("${razorpay.api.key}")
	private String razorpayApiKey;

	@Value("${razorpay.api.secret}")
	private String razorpayApiSecret;

	private final PaymentOrderRepository paymentOrderRepository;
	private final NotificationEventProducer notificationEventProducer;

	@KafkaListener(topics = "booking-failed", groupId = "payment-group")
	public void consume(BookingFailedEvent event) {
		log.warn("[Saga] ← booking-failed received: bookingId={}, reason={}",
				event.getBookingId(), event.getReason());

		PaymentOrder order = paymentOrderRepository
				.findById(event.getPaymentOrderId())
				.orElse(null);

		if (order == null) {
			log.error("[Saga] PaymentOrder not found: {}", event.getPaymentOrderId());
			return;
		}

		// Idempotency check – tránh rollback 2 lần
		if (PaymentOrderStatus.FAILED.equals(order.getStatus())) {
			log.warn("[Saga] Already rolled back, skip: {}", order.getId());
			return;
		}

		// COMPENSATION: đánh dấu thất bại + hoàn tiền
		order.setStatus(PaymentOrderStatus.FAILED);
		paymentOrderRepository.save(order);

		// TODO: gọi Razorpay/Stripe/SePay refund API ở đây
		initiateRefund(order);

		// Gửi thông báo thất bại cho user
		notificationEventProducer.sentFailedNotificationEvent(
				order.getBookingId(),
				order.getUserId(),
				order.getClinicId()
		);

		log.info("[Saga] Compensation done: paymentOrderId={}", order.getId());
	}

	private void initiateRefund(PaymentOrder order) {
		switch (order.getPaymentMethod()) {
			case STRIPE   -> refundStripe(order);
			case RAZORPAY -> refundRazorpay(order);
			case SEPAY    -> refundSepay(order);
			default       -> log.warn("[Refund] Unknown method: {}", order.getPaymentMethod());
		}
	}

	// ── Stripe ────────────────────────────────────────────────────────────────
	private void refundStripe(PaymentOrder order) {
		String paymentIntentId = order.getStripePaymentIntent();

		if (paymentIntentId == null || paymentIntentId.isBlank()) {
			log.error("[Refund][Stripe] No paymentIntentId for order={}", order.getId());
			return;
		}

		try {
			Stripe.apiKey = stripeSecretKey;

			RefundCreateParams params = RefundCreateParams.builder()
					.setPaymentIntent(paymentIntentId)
					// không set amount = refund toàn bộ
					// .setAmount(order.getAmount() * 100) nếu muốn refund một phần
					.setReason(RefundCreateParams.Reason.DUPLICATE)
					.build();

			Refund refund = Refund.create(params);

			log.info("[Refund][Stripe] SUCCESS – refundId={}, status={}, orderId={}",
					refund.getId(), refund.getStatus(), order.getId());

		} catch (StripeException e) {
			log.error("[Refund][Stripe] FAILED – orderId={}, error={}",
					order.getId(), e.getMessage());
			// Đẩy vào DLT hoặc alert team xử lý thủ công
		}
	}

	// ── Razorpay ──────────────────────────────────────────────────────────────
	private void refundRazorpay(PaymentOrder order) {
		log.warn("[Refund][Razorpay] Not yet implemented for order={}", order.getId());
	}

	// ── SePay ─────────────────────────────────────────────────────────────────
	private void refundSepay(PaymentOrder order) {
		log.warn("[Refund][SePay] Manual refund required for order={}", order.getId());
	}
}
