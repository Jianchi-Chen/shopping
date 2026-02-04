package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.OrderDTO;
import io.cjc.backend.dto.OrderDetailDTO;
import io.cjc.backend.enums.OrderStatus;
import io.cjc.backend.security.UserPrincipal;
import io.cjc.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/orders")
@RequiredArgsConstructor
public class UserOrderController {
    
    private final OrderService orderService;

    @GetMapping
    public ApiResponse<PageResponse<OrderDTO>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) OrderStatus status) {
        
        PageResponse<OrderDTO> result = orderService.getOrders(
                page, pageSize, null, status, null, null, null, principal.getUserId()
        );
        return ApiResponse.success(result);
    }
}
