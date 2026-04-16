package org.sang.client.feign;

import com.razorpay.RazorpayException;
import org.sang.constant.PaymentMethod;
import org.sang.exception.UserException;
import org.sang.model.Booking;
import org.sang.payload.response.PaymentLinkResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PAYMENT-SERVICE")
public interface PaymentFeignClient {

	@PostMapping("/api/payments/create")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(
			@RequestHeader("Authorization") String jwt,
			@RequestBody Booking booking,
			@RequestParam PaymentMethod paymentMethod) throws UserException,
			RazorpayException;
}
