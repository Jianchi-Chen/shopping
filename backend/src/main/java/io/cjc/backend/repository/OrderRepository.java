package io.cjc.backend.repository;

import io.cjc.backend.entity.Order;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.enums.PayStatus;
import io.cjc.backend.enums.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    @Query("SELECT o FROM Order o WHERE " +
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

    Page<Order> findByBuyerId(String buyerId, Pageable pageable);

    Page<Order> findByBuyerIdAndStatus(String buyerId, OrderStatus status, Pageable pageable);
}
