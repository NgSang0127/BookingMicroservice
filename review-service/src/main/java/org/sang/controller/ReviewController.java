package org.sang.controller;

import lombok.RequiredArgsConstructor;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.client.feign.UserFeignClient;
import org.sang.exception.UserException;
import org.sang.mapper.ReviewMapper;
import org.sang.model.Review;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ReviewDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.CreateReviewRequest;
import org.sang.payload.response.ApiResponse;
import org.sang.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

	private final ReviewService reviewService;
	private final UserFeignClient userService;
	private final ClinicFeignClient clinicService;

	@GetMapping("/clinic/{clinicId}")
	public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(
			@PathVariable Long clinicId) {

		List<Review> reviews = reviewService.getReviewsByClinicId(clinicId);

		List<ReviewDTO> reviewDTOS =  reviews.stream().map((review)->
				{
					UserDTO user= null;
					try {
						user = userService.getUserById(review.getUserId()).getBody();
					} catch (UserException e) {
						throw new RuntimeException(e);
					}
					return ReviewMapper.mapToDTO(review,user);
				}
		).toList();

		return ResponseEntity.ok(reviewDTOS);

	}

	@PostMapping("/clinic/{clinicId}")
	public ResponseEntity<ReviewDTO> writeReview(
			@RequestBody CreateReviewRequest req,
			@PathVariable Long clinicId,
			@RequestHeader("Authorization") String jwt) throws Exception {

		UserDTO user = userService.getUserFromJwtToken(jwt).getBody();
		ClinicDTO product = clinicService.getClinicById(clinicId).getBody();


		Review review = reviewService.createReview(
				req, user, product
		);
		UserDTO reviewer = userService.getUserById(
				review.getUserId()
		).getBody();

		ReviewDTO reviewDTO= ReviewMapper.mapToDTO(review,reviewer);

		return ResponseEntity.ok(reviewDTO);

	}

	@PatchMapping("/{reviewId}")
	public ResponseEntity<Review> updateReview(
			@RequestBody CreateReviewRequest req,
			@PathVariable Long reviewId,
			@RequestHeader("Authorization") String jwt)
			throws Exception {

		UserDTO user = userService.getUserFromJwtToken(jwt).getBody();

		Review review = reviewService.updateReview(
				reviewId,
				req.getReviewText(),
				req.getReviewRating(),
				user.getId()
		);
		return ResponseEntity.ok(review);

	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResponse> deleteReview(
			@PathVariable Long reviewId,
			@RequestHeader("Authorization") String jwt) throws Exception
	{

		UserDTO user = userService.getUserFromJwtToken(jwt).getBody();

		reviewService.deleteReview(reviewId, user.getId());
		ApiResponse res = new ApiResponse("Review deleted successfully");


		return ResponseEntity.ok(res);

	}
}
