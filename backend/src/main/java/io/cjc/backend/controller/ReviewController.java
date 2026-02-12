package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.dto.ReviewDTO;
import io.cjc.backend.security.UserPrincipal;
import io.cjc.backend.service.ReviewService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commerce/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewDTO> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateReviewRequest request) {
        ReviewDTO result = reviewService.createReview(
                request.getProductId(),
                request.getOrderId(),
                principal.getUsername(),
                principal.getUsername(),
                request.getRating(),
                request.getContent()
        );
        return ApiResponse.success(result);
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewDTO>> getProductReviews(
            @PathVariable String productId) {
        List<ReviewDTO> reviews = reviewService.getProductReviews(productId);
        return ApiResponse.success(reviews);
    }

    @GetMapping("/product/{productId}/page")
    public ApiResponse<Page<ReviewDTO>> getProductReviewsPage(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ReviewDTO> reviews = reviewService.getProductReviewsPage(productId, page, pageSize);
        return ApiResponse.success(reviews);
    }

    @Data
    public static class CreateReviewRequest {
        private String productId;
        private String orderId;
        private Integer rating;
        private String content;
    }
}
