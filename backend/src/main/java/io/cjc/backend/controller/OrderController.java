package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.CreateOrderRequest;
import io.cjc.backend.dto.OrderDTO;
import io.cjc.backend.dto.OrderDetailDTO;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.enums.PayStatus;
import io.cjc.backend.enums.RefundStatus;
import io.cjc.backend.security.UserPrincipal;
import io.cjc.backend.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commerce/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @GetMapping
    public ApiResponse<PageResponse<OrderDTO>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) PayStatus payStatus,
            @RequestParam(required = false) RefundStatus refundStatus,
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) String buyerId) {
        
        PageResponse<OrderDTO> result = orderService.getOrders(
                page, pageSize, orderNo, status, payStatus, refundStatus, shopId, buyerId
        );
        return ApiResponse.success(result);
    }

    @PostMapping("/status")
    public ApiResponse<OrderDTO> updateStatus(@RequestBody UpdateStatusRequest request) {
        OrderDTO result = orderService.updateStatus(request.getId(), request.getStatus());
        return ApiResponse.success(result);
    }

    @PostMapping("/refund")
    public ApiResponse<OrderDTO> updateRefundStatus(@RequestBody UpdateRefundRequest request) {
        OrderDTO result = orderService.updateRefundStatus(request.getId(), request.getRefundStatus());
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<OrderDetailDTO> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateOrderRequest request) {
        OrderDetailDTO result = orderService.createOrder(principal.getUsername(), request);
        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<PageResponse<OrderDetailDTO>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) OrderStatus status) {
        
        PageResponse<OrderDetailDTO> result = orderService.getMyOrders(
                principal.getUsername(), page, pageSize, status
        );
        return ApiResponse.success(result);
    }

    @Data
    public static class UpdateStatusRequest {
        private String id;
        private OrderStatus status;
    }

    @Data
    public static class UpdateRefundRequest {
        private String id;
        private RefundStatus refundStatus;
    }
}
