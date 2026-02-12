package io.cjc.backend.service;

import io.cjc.backend.dto.ReviewDTO;
import io.cjc.backend.entity.Product;
import io.cjc.backend.entity.Review;
import io.cjc.backend.repository.ProductRepository;
import io.cjc.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional
    public ReviewDTO createReview(String productId, String orderId, String reviewerId, String reviewerName, Integer rating, String content) {
        // 检查是否已存在评论
        if (reviewRepository.existsByProductIdAndOrderId(productId, orderId)) {
            throw new RuntimeException("该订单商品已评论，不能重复评论");
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setRating(Math.max(1, Math.min(5, rating)));  // 限制在1-5之间
        review.setContent(content != null ? content.substring(0, Math.min(1000, content.length())) : "");
        // 明确设置创建时间，确保不会为null
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        
        // 更新商品的评分和评论数
        updateProductRatingAndCount(productId);
        
        return toDTO(saved);
    }
    
    /**
     * 更新商品的平均评分和评论数
     */
    private void updateProductRatingAndCount(String productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setReviewCount(reviews.size());
            
            if (!reviews.isEmpty()) {
                double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
                product.setRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
            } else {
                product.setRating(BigDecimal.ZERO);
            }
            
            productRepository.save(product);
        }
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getProductReviews(String productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO> getProductReviewsPage(String productId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(this::toDTO);
    }

    private ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProductId());
        dto.setOrderId(review.getOrderId());
        dto.setReviewerId(review.getReviewerId());
        dto.setReviewerName(review.getReviewerName());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(review.getCreatedAt().format(DATE_FORMATTER));
        } else {
            dto.setCreatedAt(LocalDateTime.now().format(DATE_FORMATTER));
        }
        return dto;
    }
}
