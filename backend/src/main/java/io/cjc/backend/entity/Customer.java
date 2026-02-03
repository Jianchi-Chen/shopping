package io.cjc.backend.entity;

import io.cjc.backend.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Column(name = "order_count")
    private Integer orderCount = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

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
