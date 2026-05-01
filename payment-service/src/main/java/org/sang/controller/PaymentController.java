package org.sang.controller;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.client.feign.UserFeignClient;
import org.sang.constant.PaymentMethod;
import org.sang.exception.UserException;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.response.PaymentLinkResponse;
import org.sang.payload.response.SePayIpnPayload;
import org.sang.service.PaymentService;
import org.sang.service.impl.PaymentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService     paymentService;
	private final PaymentServiceImpl paymentServiceImpl;
	private final UserFeignClient    userService;

	// ─────────────────────────────────────────────────────────────────────────
	// Tạo đơn thanh toán  (Razorpay | Stripe | SePay)
	// ─────────────────────────────────────────────────────────────────────────
	@PostMapping("/create")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(
			@RequestBody BookingDTO booking,
			@RequestParam PaymentMethod paymentMethod)
			throws UserException, RazorpayException, StripeException {

		UserDTO user = userService.getUserProfile().getBody();
		PaymentLinkResponse res = paymentService.createOrder(user, booking, paymentMethod);
		return ResponseEntity.ok(res);
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Lấy PaymentOrder theo ID
	// ─────────────────────────────────────────────────────────────────────────
	@GetMapping("/{paymentOrderId}")
	public ResponseEntity<PaymentOrder> getPaymentOrderById(
			@PathVariable Long paymentOrderId) {
		try {
			return ResponseEntity.ok(paymentService.getPaymentOrderById(paymentOrderId));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	// ─────────────────────────────────────────────────────────────────────────
	// Xác nhận thanh toán (Razorpay / Stripe)
	// ─────────────────────────────────────────────────────────────────────────
	@PatchMapping("/proceed")
	public ResponseEntity<Boolean> proceedPayment(
			@RequestParam String paymentId,
			@RequestParam String paymentLinkId) throws Exception {

		PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentLinkId);
		Boolean success = paymentService.ProceedPaymentOrder(paymentOrder, paymentId, paymentLinkId);
		return ResponseEntity.ok(success);
	}

	// =========================================================================
	// SePay IPN
	//
	// Cấu hình URL này tại my.sepay.vn → Cổng thanh toán → IPN:
	//   https://<ngrok-url>/api/payments/sepay/ipn
	//
	// SePay gửi POST với header: X-Secret-Key: <your_secret_key>
	// Endpoint phải trả HTTP 200, nếu không SePay sẽ retry.
	// =========================================================================
	@PostMapping("/sepay/ipn")
	public ResponseEntity<Map<String, Object>> sePayIpn(
			@RequestBody SePayIpnPayload payload,
			@RequestHeader(value = "X-Secret-Key", required = false) String secretKey) {

		log.info("[SePay IPN] Received: notification_type={}", payload.getNotificationType());

		boolean success = paymentServiceImpl.handleSePayIpn(payload, secretKey);

		// Luôn trả 200 (kể cả khi đơn đã xử lý) để SePay không retry
		return ResponseEntity.ok(Map.of("success", success));
	}

	// ─────────────────────────────────────────────────────────────────────────
	// SePay Redirect callbacks (success / error / cancel)
	// SePay redirect trình duyệt về các URL này sau khi thanh toán.
	// Nên redirect về frontend thay vì trả JSON.
	// ─────────────────────────────────────────────────────────────────────────

	@GetMapping("/sepay/success")
	public ResponseEntity<Map<String, Object>> sePaySuccess(
			@RequestParam(required = false) String order_invoice_number,
			@RequestParam(required = false) String transaction_id) {

		log.info("[SePay] success – invoice={}, txId={}", order_invoice_number, transaction_id);
		// TODO: redirect về frontend, ví dụ:
		// return ResponseEntity.status(302)
		//     .header("Location", "http://localhost:5173/payment-success?invoice=" + order_invoice_number)
		//     .build();
		return ResponseEntity.ok(Map.of(
				"status",  "success",
				"invoice", String.valueOf(order_invoice_number)
		));
	}

	@GetMapping("/sepay/error")
	public ResponseEntity<Map<String, Object>> sePayError(
			@RequestParam(required = false) String order_invoice_number) {

		log.warn("[SePay] error – invoice={}", order_invoice_number);
		return ResponseEntity.ok(Map.of(
				"status",  "error",
				"invoice", String.valueOf(order_invoice_number)
		));
	}

	@GetMapping("/sepay/cancel")
	public ResponseEntity<Map<String, Object>> sePayCancel(
			@RequestParam(required = false) String order_invoice_number) {

		log.info("[SePay] cancel – invoice={}", order_invoice_number);
		return ResponseEntity.ok(Map.of(
				"status",  "cancelled",
				"invoice", String.valueOf(order_invoice_number)
		));
	}
}