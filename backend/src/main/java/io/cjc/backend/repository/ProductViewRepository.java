package io.cjc.backend.repository;

import io.cjc.backend.entity.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, String> {
    long countByViewedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByShopIdAndViewedAtBetween(String shopId, LocalDateTime start, LocalDateTime end);

    long countByShopId(String shopId);
}
