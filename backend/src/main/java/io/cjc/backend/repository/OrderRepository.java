package io.cjc.backend.repository;

import io.cjc.backend.entity.Order;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.enums.PayStatus;
import io.cjc.backend.enums.RefundStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE " +
            "(:orderNo IS NULL OR o.orderNo LIKE %:orderNo%) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:payStatus IS NULL OR o.payStatus = :payStatus) AND " +
            "(:refundStatus IS NULL OR o.refundStatus = :refundStatus) AND " +
            "(:shopId IS NULL OR o.shopId = :shopId) AND " +
            "(:buyerId IS NULL OR o.buyerId = :buyerId)")
    Page<Order> findByFilters(
            @Param("orderNo") String orderNo,
            @Param("status") OrderStatus status,
            @Param("payStatus") PayStatus payStatus,
            @Param("refundStatus") RefundStatus refundStatus,
            @Param("shopId") String shopId,
            @Param("buyerId") String buyerId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.buyerId = :buyerId ORDER BY o.createdAt DESC")
    Page<Order> findByBuyerId(@Param("buyerId") String buyerId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.buyerId = :buyerId AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByBuyerIdAndStatus(@Param("buyerId") String buyerId, @Param("status") OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    long countByShopIdAndStatus(String shopId, OrderStatus status);

    long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);

    long countByShopIdAndStatusAndCreatedAtBetween(String shopId, OrderStatus status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.createdAt >= :start AND o.createdAt < :end")
    BigDecimal sumTotalAmountByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.shopId = :shopId AND o.createdAt >= :start AND o.createdAt < :end")
    BigDecimal sumTotalAmountByShopIdAndStatusAndCreatedAtBetween(
            @Param("shopId") String shopId,
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.shopId = :shopId")
    BigDecimal sumTotalAmountByShopIdAndStatus(
            @Param("shopId") String shopId,
            @Param("status") OrderStatus status
    );
}
