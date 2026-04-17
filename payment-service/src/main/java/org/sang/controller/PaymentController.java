package org.sang.controller;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.sang.client.feign.UserFeignClient;
import org.sang.constant.PaymentMethod;
import org.sang.exception.UserException;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.response.PaymentLinkResponse;
import org.sang.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final UserFeignClient userService;


	@PostMapping("/create")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(
			@RequestHeader("Authorization") String jwt,
			@RequestBody BookingDTO booking,
			@RequestParam PaymentMethod paymentMethod) throws UserException,
			RazorpayException, StripeException {

		System.out.println("------"+booking);

		UserDTO user = userService.getUserFromJwtToken(jwt).getBody();

		PaymentLinkResponse paymentLinkResponse = paymentService
				.createOrder(user, booking, paymentMethod);

		return ResponseEntity.ok(paymentLinkResponse);
	}

	@GetMapping("/{paymentOrderId}")
	public ResponseEntity<PaymentOrder> getPaymentOrderById(
			@PathVariable Long paymentOrderId) {
		try {
			PaymentOrder paymentOrder = paymentService.getPaymentOrderById(paymentOrderId);
			return ResponseEntity.ok(paymentOrder);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping("/proceed")
	public ResponseEntity<Boolean> proceedPayment(
			@RequestParam String paymentId,
			@RequestParam String paymentLinkId) throws Exception {

		PaymentOrder paymentOrder = paymentService.
				getPaymentOrderByPaymentId(paymentLinkId);
		Boolean success = paymentService.ProceedPaymentOrder(
				paymentOrder,
				paymentId, paymentLinkId);
		return ResponseEntity.ok(success);

	}


}
