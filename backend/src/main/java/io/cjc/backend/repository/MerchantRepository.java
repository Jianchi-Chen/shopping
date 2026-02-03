package io.cjc.backend.repository;

import io.cjc.backend.entity.Merchant;
import io.cjc.backend.enums.MerchantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String> {
    
    Optional<Merchant> findByShopId(String shopId);
    
    @Query("SELECT m FROM Merchant m WHERE " +
            "(:keyword IS NULL OR m.shopName LIKE %:keyword% OR m.ownerName LIKE %:keyword%) AND " +
            "(:status IS NULL OR m.status = :status)")
    Page<Merchant> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") MerchantStatus status,
            Pageable pageable
    );
}
