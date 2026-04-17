package org.sang.service;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import org.sang.constant.PaymentMethod;
import org.sang.exception.UserException;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.BookingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.response.PaymentLinkResponse;

public interface PaymentService {
	PaymentLinkResponse createOrder(UserDTO user,
			BookingDTO booking, PaymentMethod paymentMethod) throws RazorpayException, UserException, StripeException;

	PaymentOrder getPaymentOrderById(Long id) throws Exception;

	PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception;

	Boolean ProceedPaymentOrder (PaymentOrder paymentOrder,
			String paymentId, String paymentLinkId) throws RazorpayException;

	PaymentLink createRazorpayPaymentLink(UserDTO user,
			Long Amount,
			Long orderId) throws RazorpayException;

	String createStripePaymentLink(UserDTO user, Long Amount,
			Long orderId) throws StripeException;
}
