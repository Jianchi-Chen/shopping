package io.cjc.backend.repository;

import io.cjc.backend.entity.Product;
import io.cjc.backend.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.id LIKE %:keyword%) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:shopId IS NULL OR p.shopId = :shopId)")
    Page<Product> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") ProductStatus status,
            @Param("category") String category,
            @Param("shopId") String shopId,
            Pageable pageable
    );

    long countByCategory(String category);
}
