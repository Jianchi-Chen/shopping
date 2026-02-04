package io.cjc.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressDTO {
    private String id;
    private String userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String addressDetail;
    private String postalCode;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
