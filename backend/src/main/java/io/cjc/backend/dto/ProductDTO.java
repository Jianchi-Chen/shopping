package io.cjc.backend.dto;

import io.cjc.backend.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String title;
    private String sku;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private ProductStatus status;
    private String category;
    private String shopId;
    private String shopName;
    private String updatedAt;
}
