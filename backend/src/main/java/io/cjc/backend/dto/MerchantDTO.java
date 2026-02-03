package io.cjc.backend.dto;

import io.cjc.backend.enums.MerchantStatus;
import lombok.Data;

@Data
public class MerchantDTO {
    private String id;
    private String shopId;
    private String shopName;
    private String ownerName;
    private String contactPhone;
    private MerchantStatus status;
    private String createdAt;
}
