package io.cjc.backend.enums;

public enum OrderStatus {
    PENDING_PAYMENT,    // 待支付
    PENDING_SHIPMENT,   // 待发货
    SHIPPED,            // 已发货
    COMPLETED,          // 已完成
    CLOSED,             // 已关闭
    AFTER_SALE          // 售后中
}
