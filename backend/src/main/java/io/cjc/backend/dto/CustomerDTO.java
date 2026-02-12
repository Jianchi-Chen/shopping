package io.cjc.backend.dto;

import io.cjc.backend.enums.CustomerStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDTO {
    private String id;
    private String name;
    private String username;  // 从关联的User获取
    private String email;
    private String phone;
    private String avatar;  // 用户头像
    private CustomerStatus status;
    private Integer orderCount;
    private BigDecimal totalSpent;
    private String createdAt;
}
