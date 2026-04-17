package org.sang.service.impl;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
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

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	@Value("${stripe.api.key}")
	private String stripeSecretKey;

	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecret;

	private final PaymentOrderRepository paymentOrderRepository;
	private final NotificationEventProducer notificationEventProducer;
	private final BookingEventProducer bookingEventProducer;

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
		PaymentOrder paymentOrder = paymentOrderRepository.save(order);

		PaymentLinkResponse res = new PaymentLinkResponse();
		if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
			PaymentLink payment = createRazorpayPaymentLink(user,
					paymentOrder.getAmount(),
					paymentOrder.getId());
			String paymentUrl = payment.get("short_url");
			String paymentUrlId = payment.get("id");

			res.setPayment_link_url(paymentUrl);
			paymentOrder.setPaymentLinkId(paymentUrlId);
			paymentOrderRepository.save(paymentOrder);
		} else {
			String paymentUrl = createStripePaymentLink(user,
					paymentOrder.getAmount(),
					paymentOrder.getId());

			res.setPayment_link_url(paymentUrl);
		}

		return res;
	}

	@Override
	public PaymentOrder getPaymentOrderById(Long id) throws Exception {
		Optional<PaymentOrder> optionalPaymentOrder = paymentOrderRepository.findById(id);
		if (optionalPaymentOrder.isEmpty()) {
			throw new Exception("payment order not found with id " + id);
		}
		return optionalPaymentOrder.get();
	}

	@Override
	public PaymentOrder getPaymentOrderByPaymentId(String paymentLinkId) throws Exception {
		PaymentOrder paymentOrder = paymentOrderRepository
				.findByPaymentLinkId(paymentLinkId);

		if (paymentOrder == null) {
			throw new Exception("payment order not found with id " + paymentLinkId);
		}
		return paymentOrder;
	}

	@Override
	public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder,
			String paymentId,
			String paymentLinkId) throws RazorpayException {

		if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {

			if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)) {

				RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
				Payment payment = razorpay.payments.fetch(paymentId);

				Integer amount = payment.get("amount");
				String status = payment.get("status");

				if (status.equals("captured")) {
					notificationEventProducer.sentNotificationEvent(
							paymentOrder.getBookingId(),
							paymentOrder.getUserId(),
							paymentOrder.getClinicId()
					);

					bookingEventProducer.sentBookingUpdateEvent(paymentOrder);

					paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
					paymentOrderRepository.save(paymentOrder);

					return true;
				}
				paymentOrder.setStatus(PaymentOrderStatus.FAILED);
				paymentOrderRepository.save(paymentOrder);
				return false;
			} else {

				paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
				paymentOrderRepository.save(paymentOrder);

				return true;
			}

		}

		return false;
	}

	@Override
	public PaymentLink createRazorpayPaymentLink(UserDTO user,
			Long Amount,
			Long orderId
	)
			throws RazorpayException {

		Long amount = Amount * 100;

		try {
			// Instantiate a Razorpay client with your key ID and secret
			RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

			JSONObject paymentLinkRequest = new JSONObject();
			paymentLinkRequest.put("amount", amount);
			paymentLinkRequest.put("currency", "INR");

			// Create a JSON object with the customer details
			JSONObject customer = new JSONObject();
			customer.put("name", user.getFullName());

			customer.put("email", user.getEmail());
			paymentLinkRequest.put("customer", customer);

			// Create a JSON object with the notification settings
			JSONObject notify = new JSONObject();
			notify.put("email", true);
			paymentLinkRequest.put("notify", notify);

			// Set the reminder settings
			paymentLinkRequest.put("reminder_enable", true);

			// Set the callback URL and method
			paymentLinkRequest.put("callback_url", "http://localhost:3173/payment-success/" + orderId);
			paymentLinkRequest.put("callback_method", "get");

			PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

			return payment;

		} catch (RazorpayException e) {

			System.out.println("Error creating payment link: " + e.getMessage());
			throw new RazorpayException(e.getMessage());
		}
	}

	@Override
	public String createStripePaymentLink(UserDTO user,
			Long amount,
			Long orderId) throws StripeException {
		Stripe.apiKey = stripeSecretKey;

		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl("http://localhost:5173/payment-success/" + orderId)
				.setCancelUrl("http://localhost:5173/payment/cancel")
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setQuantity(1L)
						.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
								.setCurrency("usd")
								.setUnitAmount(amount * 100)
								.setProductData(SessionCreateParams
										.LineItem
										.PriceData
										.ProductData
										.builder()
										.setName("Top up wallet")
										.build()
								).build()
						).build()
				).build();

		Session session = Session.create(params);

		return session.getUrl();
	}
}
