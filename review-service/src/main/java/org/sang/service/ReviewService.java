package org.sang.service;

import javax.naming.AuthenticationException;
import java.util.List;
import org.sang.model.Review;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.CreateReviewRequest;

public interface ReviewService {

	Review createReview(CreateReviewRequest req,
			UserDTO user,
			ClinicDTO clinic);

	List<Review> getReviewsByClinicId(Long productId);

	Review updateReview(Long reviewId,
			String reviewText,
			double rating,
			Long userId) throws Exception, AuthenticationException;


	void deleteReview(Long reviewId, Long userId) throws Exception, AuthenticationException;

}
