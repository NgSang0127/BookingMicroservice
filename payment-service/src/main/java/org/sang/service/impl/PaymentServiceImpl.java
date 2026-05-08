package org.sang.service.impl;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.sang.config.SePayProperties;
import org.sang.constant.PaymentMethod;
import org.sang.constant.PaymentOrderStatus;
import org.sang.exception.UserException;
import org.sang.messaging.BookingEventProducer;
import org.sang.messaging.NotificationEventProducer;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.response.PaymentLinkResponse;
import org.sang.repository.PaymentOrderRepository;
import org.sang.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	// ── Stripe ────────────────────────────────────────────────────────────────
	@Value("${stripe.secret-key}")
	private String stripeSecretKey;

	// ── Razorpay ──────────────────────────────────────────────────────────────
	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecret;

	// ── SePay ─────────────────────────────────────────────────────────────────
	private final SePayProperties sePayProperties;

	private final PaymentOrderRepository paymentOrderRepository;
	private final NotificationEventProducer notificationEventProducer;
	private final BookingEventProducer bookingEventProducer;

	// =========================================================================
	// createOrder
	// =========================================================================
	@Override
	public PaymentLinkResponse createOrder(
			UserDTO user,
			BookingDTO booking,
			PaymentMethod paymentMethod) throws RazorpayException, UserException, StripeException {

		Long amount = (long) booking.getTotalPrice();

		PaymentOrder order = new PaymentOrder();
		order.setUserId(user.getId());
		order.setAmount(amount);
		order.setBookingId(booking.getId());
		order.setClinicId(booking.getClinicId());
		order.setPaymentMethod(paymentMethod);
		PaymentOrder saved = paymentOrderRepository.save(order);

		PaymentLinkResponse res = new PaymentLinkResponse();
		log.info("Payment method: {}", paymentMethod);
		if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
			PaymentLink payment = createRazorpayPaymentLink(user, saved.getAmount(), saved.getId());
			String paymentUrl = payment.get("short_url");
			String paymentUrlId = payment.get("id");
			res.setPayment_link_url(paymentUrl);
			saved.setPaymentLinkId(paymentUrlId);
			paymentOrderRepository.save(saved);

		} else if (paymentMethod.equals(PaymentMethod.STRIPE)) {
			String paymentUrl = createStripePaymentLink(user, saved.getAmount(), saved.getId());
			res.setPayment_link_url(paymentUrl);

		} else if (paymentMethod.equals(PaymentMethod.SEPAY)) {
			log.info("[SePay] Creating order for booking id={}, amount={}",
					saved.getId(), saved.getAmount()); // Kiểm tra amount có null không

			String invoiceNumber = "INV-" + saved.getId();
			saved.setPaymentLinkId(invoiceNumber);
			paymentOrderRepository.save(saved);

			String checkoutUrl = buildSePayCheckoutUrl(saved, invoiceNumber);
			log.info("[SePay] Checkout URL: {}", checkoutUrl); // Kiểm tra URL có build được không
			res.setPayment_link_url(checkoutUrl);
		}

		return res;
	}

	// =========================================================================
	// SePay – tạo URL form checkout (POST form hoặc redirect GET)
	//
	// Theo tài liệu SePay, thứ tự field khi ký phải đúng:
	// order_amount, merchant, currency, operation, order_description,
	// order_invoice_number, [customer_id], [payment_method],
	// [success_url], [error_url], [cancel_url]
	// =========================================================================
	public String buildSePayCheckoutUrl(PaymentOrder order, String invoiceNumber) {
		String successUrl = "https://whiff-impromptu-syrup.ngrok-free.dev/api/payments/sepay/success";
		String errorUrl = "https://whiff-impromptu-syrup.ngrok-free.dev/api/payments/sepay/error";
		String cancelUrl = "https://whiff-impromptu-syrup.ngrok-free.dev/api/payments/sepay/cancel";
		String description = "Thanh toan don hang " + invoiceNumber;

		// LinkedHashMap giữ đúng thứ tự theo spec SePay
		Map<String, String> fields = new LinkedHashMap<>();
		fields.put("order_amount", String.valueOf(order.getAmount()));
		fields.put("merchant", sePayProperties.getMerchantId());
		fields.put("currency", "VND");
		fields.put("operation", "PURCHASE");
		fields.put("order_description", description);
		fields.put("order_invoice_number", invoiceNumber);
		fields.put("success_url", successUrl);
		fields.put("error_url", errorUrl);
		fields.put("cancel_url", cancelUrl);

		String signature = signFields(fields, sePayProperties.getSecretKey());

		// Build query string để redirect (hoặc dùng để render form hidden inputs)
		StringBuilder sb = new StringBuilder(sePayProperties.getCheckoutUrl()).append("?");
		fields.forEach((k, v) -> sb.append(k).append("=").append(urlEncode(v)).append("&"));
		sb.append("signature=").append(urlEncode(signature));

		return sb.toString();
	}

	/**
	 * Tạo chữ ký HMAC-SHA256 theo spec SePay: 1. Nối các field: "field1=value1,field2=value2,..." 2. base64(
	 * HMAC-SHA256(chuỗi, secretKey) )
	 */
	private String signFields(Map<String, String> fields, String secretKey) {
		StringJoiner joiner = new StringJoiner(",");
		fields.forEach((k, v) -> joiner.add(k + "=" + v));
		String signedString = joiner.toString();

		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] hash = mac.doFinal(signedString.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create SePay signature", e);
		}
	}

	// =========================================================================
	// SePay – xử lý IPN
	// SePay gửi: POST {ipn_url}
	// Header: X-Secret-Key: <secret_key>
	// =========================================================================
	public boolean handleSePayIpn(org.sang.payload.response.SePayIpnPayload payload, String receivedSecretKey) {

		// 1. Xác thực secret key từ header X-Secret-Key
		if (!sePayProperties.getSecretKey().equals(receivedSecretKey)) {
			log.warn("[SePay IPN] Invalid X-Secret-Key, rejecting");
			return false;
		}

		if (payload == null) {
			log.warn("[SePay IPN] Empty payload");
			return false;
		}

		log.info("[SePay IPN] notification_type={}, invoice={}",
				payload.getNotificationType(),
				payload.getOrder() != null ? payload.getOrder().getOrderInvoiceNumber() : "null");

		// 2. Chỉ xử lý ORDER_PAID
		if (!"ORDER_PAID".equals(payload.getNotificationType())) {
			log.info("[SePay IPN] Ignored notification: {}", payload.getNotificationType());
			return true; // vẫn trả 200 để SePay không retry
		}

		String invoiceNumber = payload.getOrder().getOrderInvoiceNumber();
		PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(invoiceNumber);

		if (paymentOrder == null) {
			log.error("[SePay IPN] PaymentOrder not found for invoice: {}", invoiceNumber);
			return false;
		}

		if (!PaymentOrderStatus.PENDING.equals(paymentOrder.getStatus())) {
			log.warn("[SePay IPN] Order {} already processed ({})", invoiceNumber, paymentOrder.getStatus());
			return true; // idempotent – trả 200 để SePay không retry
		}

		// 3. Kiểm tra order_status từ SePay
		String orderStatus = payload.getOrder().getOrderStatus();
		String txStatus = payload.getTransaction() != null
				? payload.getTransaction().getTransactionStatus() : "";

		if ("CAPTURED".equalsIgnoreCase(orderStatus) && "APPROVED".equalsIgnoreCase(txStatus)) {
			paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
			paymentOrderRepository.save(paymentOrder);

			notificationEventProducer.sentNotificationEvent(
					paymentOrder.getBookingId(),
					paymentOrder.getUserId(),
					paymentOrder.getClinicId());
			bookingEventProducer.sentBookingUpdateEvent(paymentOrder);

			log.info("[SePay IPN] Payment SUCCESS – orderId={}", paymentOrder.getId());
			return true;
		}

		paymentOrder.setStatus(PaymentOrderStatus.FAILED);
		paymentOrderRepository.save(paymentOrder);
		log.warn("[SePay IPN] Payment FAILED – orderStatus={}, txStatus={}", orderStatus, txStatus);
		return false;
	}

	// =========================================================================
	// Các method có sẵn (Razorpay / Stripe / getters)
	// =========================================================================
	@Override
	public PaymentOrder getPaymentOrderById(Long id) throws Exception {
		return paymentOrderRepository.findById(id)
				.orElseThrow(() -> new Exception("payment order not found with id " + id));
	}

	@Override
	public PaymentOrder getPaymentOrderByPaymentId(String paymentLinkId) throws Exception {
		PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(paymentLinkId);
		if (paymentOrder == null) {
			throw new Exception("payment order not found with id " + paymentLinkId);
		}
		return paymentOrder;
	}

	@Override
	public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder,
			String paymentId,
			String paymentLinkId) throws RazorpayException {

		if (!paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
			return false;
		}

		if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)) {
			RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
			Payment payment = razorpay.payments.fetch(paymentId);
			String status = payment.get("status");

			if ("captured".equals(status)) {
				notificationEventProducer.sentNotificationEvent(
						paymentOrder.getBookingId(),
						paymentOrder.getUserId(),
						paymentOrder.getClinicId());
				bookingEventProducer.sentBookingUpdateEvent(paymentOrder);
				paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
				paymentOrderRepository.save(paymentOrder);
				return true;
			}
			paymentOrder.setStatus(PaymentOrderStatus.FAILED);
			paymentOrderRepository.save(paymentOrder);
			return false;

		} else {
			try {
				Stripe.apiKey = stripeSecretKey;
				Session session = Session.retrieve(paymentLinkId);
				String paymentIntentId = session.getPaymentIntent(); // "pi_xxx"

				paymentOrder.setStripePaymentIntent(paymentIntentId); // lưu lại
			} catch (StripeException e) {
				log.error("[Stripe] Failed to retrieve session: {}", e.getMessage());
			}
			paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
			paymentOrderRepository.save(paymentOrder);

			// ✅ Thêm vào đây
			notificationEventProducer.sentNotificationEvent(
					paymentOrder.getBookingId(),
					paymentOrder.getUserId(),
					paymentOrder.getClinicId());
			bookingEventProducer.sentBookingUpdateEvent(paymentOrder);

			return true;
		}
	}

	@Override
	public PaymentLink createRazorpayPaymentLink(UserDTO user, Long Amount, Long orderId)
			throws RazorpayException {
		Long amount = Amount * 100;
		RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
		JSONObject req = new JSONObject();
		req.put("amount", amount);
		req.put("currency", "INR");
		JSONObject customer = new JSONObject();
		customer.put("name", user.getFullName());
		customer.put("email", user.getEmail());
		req.put("customer", customer);
		JSONObject notify = new JSONObject();
		notify.put("email", true);
		req.put("notify", notify);
		req.put("reminder_enable", true);
		req.put("callback_url", "http://localhost:3173/payment-success/" + orderId);
		req.put("callback_method", "get");
		return razorpay.paymentLink.create(req);
	}

	@Override
	public String createStripePaymentLink(UserDTO user, Long amount, Long orderId)
			throws StripeException {
		Stripe.apiKey = stripeSecretKey;

		// Quy đổi VND → USD (tỷ giá tạm thời, nên lấy từ API thực tế)
		// 1 USD ≈ 25,000 VND
		long amountInUsd = Math.max(1L, amount / 25000);

		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl("http://localhost:5173/payment-success/" + orderId)
				.setCancelUrl("http://localhost:5173/payment/cancel")
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setQuantity(1L)
						.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
								.setCurrency("usd")
								.setUnitAmount(amountInUsd * 100) // Stripe tính theo cents
								.setProductData(SessionCreateParams.LineItem.PriceData.ProductData
										.builder()
										.setName("Đặt lịch khám")
										.setDescription("Booking ID: " + orderId +
												" | " + amount + " VND")
										.build())
								.build())
						.build())
				.build();

		return Session.create(params).getUrl();
	}

	// ── Utilities ─────────────────────────────────────────────────────────────
	private String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			return value;
		}
	}
}