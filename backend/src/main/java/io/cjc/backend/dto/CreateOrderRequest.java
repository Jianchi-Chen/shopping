package io.cjc.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private ShippingAddress shippingAddress;
    private String remark;

    @Data
    public static class OrderItemRequest {
        private String productId;
        private Integer quantity;
        private Map<String, String> selectedSpecs;
    }

    @Data
    public static class ShippingAddress {
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detail;
    }
}
