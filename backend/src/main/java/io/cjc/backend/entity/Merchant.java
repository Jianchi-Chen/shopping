package io.cjc.backend.entity;

import io.cjc.backend.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
@Data
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "shop_id", nullable = false, unique = true)
    private String shopId;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status = MerchantStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
