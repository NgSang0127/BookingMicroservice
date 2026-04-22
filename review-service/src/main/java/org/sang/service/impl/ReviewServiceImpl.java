package org.sang.service.impl;

import lombok.RequiredArgsConstructor;
import org.sang.model.Review;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.CreateReviewRequest;
import org.sang.repository.ReviewRepository;
import org.sang.service.ReviewService;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;

	@Override
	public Review createReview(CreateReviewRequest req,
			UserDTO user,
			ClinicDTO clinic) {
		Review newReview = new Review();

		newReview.setReviewText(req.getReviewText());
		newReview.setRating(req.getReviewRating());
		newReview.setUserId(user.getId());
		newReview.setClinicId(clinic.getId());

		return reviewRepository.save(newReview);
	}

	@Override
	public List<Review> getReviewsByClinicId(Long productId) {
		return reviewRepository.findReviewsByClinicId(productId);
	}

	@Override
	public Review updateReview(Long reviewId,
			String reviewText,
			double rating,
			Long userId) throws Exception, AuthenticationException {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new Exception("Review Not found"));

		if (review.getUserId() != userId) {
			throw new AuthenticationException("You do not have permission to delete this review");
		}

		review.setReviewText(reviewText);
		review.setRating(rating);
		return reviewRepository.save(review);
	}

	@Override
	public void deleteReview(Long reviewId, Long userId) throws Exception,
			AuthenticationException {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new Exception("Review Not found"));
		if (review.getUserId() != userId) {
			throw new AuthenticationException("You do not have permission to delete this review");
		}
		reviewRepository.delete(review);
	}

}
