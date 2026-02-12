package io.cjc.backend.dto;

import io.cjc.backend.enums.MerchantStatus;
import lombok.Data;

@Data
public class MerchantDTO {
    private String id;
    private String shopId;
    private String shopName;
    private String name;  // 映射自shopName，为前端兼容
    private String ownerName;
    private String contactName;  // 映射自ownerName，为前端兼容
    private String contactPhone;
    private MerchantStatus status;
    private String createdAt;
}
