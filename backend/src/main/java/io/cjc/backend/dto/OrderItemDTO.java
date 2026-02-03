package io.cjc.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private String productId;
    private String title;
    private Integer quantity;
    private BigDecimal price;
}
