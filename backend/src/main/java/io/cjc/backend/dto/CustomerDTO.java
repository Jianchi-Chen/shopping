package io.cjc.backend.dto;

import io.cjc.backend.enums.CustomerStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDTO {
    private String id;
    private String name;
    private String phone;
    private CustomerStatus status;
    private Integer orderCount;
    private BigDecimal totalSpent;
    private String createdAt;
}
