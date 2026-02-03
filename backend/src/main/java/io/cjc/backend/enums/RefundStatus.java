package io.cjc.backend.enums;

public enum RefundStatus {
    NONE,       // 无售后
    REQUESTED,  // 已申请
    APPROVED,   // 已同意
    REJECTED,   // 已拒绝
    REFUNDED    // 已退款
}
