package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.security.JwtTokenProvider;
import io.cjc.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANT')")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestParam(required = false) Long shopId,
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.replace("Bearer ", "");
        String role = jwtTokenProvider.getRoleFromToken(jwt);
        String merchantIdStr = jwtTokenProvider.getMerchantIdFromToken(jwt);
        
        // 商家只能查看自己的数据
        if ("MERCHANT".equals(role) && merchantIdStr != null) {
            shopId = Long.parseLong(merchantIdStr);
        }
        
        Map<String, Object> statistics = dashboardService.getStatistics(shopId);
        return ApiResponse.success(statistics);
    }

    /**
     * 获取待办列表
     */
    @GetMapping("/todos")
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANT')")
    public ApiResponse<List<Map<String, Object>>> getTodoList(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.replace("Bearer ", "");
        String userIdStr = jwtTokenProvider.getUserIdFromToken(jwt);
        Long userId = Long.parseLong(userIdStr);
        
        List<Map<String, Object>> todos = dashboardService.getTodoList(userId);
        return ApiResponse.success(todos);
    }
}
