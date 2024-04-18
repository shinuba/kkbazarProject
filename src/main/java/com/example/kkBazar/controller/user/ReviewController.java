package com.example.kkBazar.controller.user;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.Review;
import com.example.kkBazar.service.user.ReviewService;

@RestController
@CrossOrigin(origins ="*")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("/review/save")
	public ResponseEntity<Object> saveReviewDetails(@RequestBody Review review) {
	    try {
	        Long reviewId = review.getReviewId();

	        if (isNullOrEmpty(review.getStarRate())) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Rate.");
	        }

	        int starRate = review.getStarRate();
	        if (starRate < 1 || starRate > 5) {
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("Message", "StarRate must be between 1 to 5.");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	        }
	        review.setDate(LocalDate.now());
	        starRate = Math.max(1, Math.min(5, starRate));
	        review.setStarRate(starRate);
	        reviewService.SaveReview(review);

	        long newReviewId = review.getReviewId();
	        Map<String, Object> successResponse = new HashMap<>();
	        successResponse.put("message", "Thank you for your review!");
	        successResponse.put("reviewId", newReviewId);

	        return ResponseEntity.ok(successResponse);
	    } catch (Exception e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "Error saving review: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	private boolean isNullOrEmpty(int starRate) {
		return false;
	}


	@PutMapping("/review/edit/{reviewId}")
	public ResponseEntity<Object> updateReview(@PathVariable("reviewId") Long reviewId,
			@RequestBody Review review) {
		try {
			
			int starRate = review.getStarRate();

	        if (starRate < 1 || starRate > 5) {
	            return ResponseEntity.badRequest().body("starRate must be between 1 to 5.");
	        }

	        starRate = Math.max(1, Math.min(5, starRate));
	        review.setStarRate(starRate);
			Review existingReview = reviewService.findById(reviewId);

			if (existingReview == null) {
				return ResponseEntity.notFound().build();
			}

		existingReview.setStarRate(review.getStarRate());

			   reviewService.SaveReview(review);
	     
		        Map<String, Object> successResponse = new HashMap<>();
		        successResponse.put("message", "Thank you for your review!");
		        return ResponseEntity.ok(successResponse);
		    } catch (Exception e) {
		        Map<String, Object> errorResponse = new HashMap<>();
		        errorResponse.put("message", "Error saving review: " + e.getMessage());
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		    }
		}
	

}
