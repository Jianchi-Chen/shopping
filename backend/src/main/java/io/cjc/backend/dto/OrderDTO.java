package io.cjc.backend.dto;

import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.enums.PayStatus;
import io.cjc.backend.enums.RefundStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private String orderNo;
    private OrderStatus status;
    private PayStatus payStatus;
    private RefundStatus refundStatus;
    private BigDecimal totalAmount;
    private Integer itemCount;
    private String buyerName;
    private String shopId;
    private String shopName;
    private String createdAt;
    private List<OrderItemDTO> items;
}
