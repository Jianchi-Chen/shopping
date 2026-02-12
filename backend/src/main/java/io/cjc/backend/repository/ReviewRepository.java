package io.cjc.backend.repository;

import io.cjc.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByProductIdOrderByCreatedAtDesc(String productId);
    
    Page<Review> findByProductIdOrderByCreatedAtDesc(String productId, Pageable pageable);
    
    List<Review> findByOrderId(String orderId);
    
    boolean existsByProductIdAndOrderId(String productId, String orderId);
}
