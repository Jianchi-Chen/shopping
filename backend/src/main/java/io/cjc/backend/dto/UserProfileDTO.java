package io.cjc.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class UserProfileDTO {
    private String id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private String merchantId;  // 商家ID（仅商家用户有值）
    private String createdAt;
    private String updatedAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt.format(FORMATTER) : null;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt.format(FORMATTER) : null;
    }
}
