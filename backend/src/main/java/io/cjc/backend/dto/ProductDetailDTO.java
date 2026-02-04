package io.cjc.backend.dto;

import io.cjc.backend.enums.ProductStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailDTO {
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
    private List<String> images;
    private String description;
    private List<ProductSpec> specs;
    private BigDecimal rating;
    private Integer reviewCount;
    private String createdAt;
    private String updatedAt;

    @Data
    public static class ProductSpec {
        private String name;
        private List<SpecOption> options;
    }

    @Data
    public static class SpecOption {
        private String name;
        private String value;
        private Boolean available;
    }
}
