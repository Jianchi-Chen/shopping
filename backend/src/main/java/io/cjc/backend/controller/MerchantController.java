package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.MerchantDTO;
import io.cjc.backend.enums.MerchantStatus;
import io.cjc.backend.service.MerchantService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity/merchants")
@RequiredArgsConstructor
public class MerchantController {
    
    private final MerchantService merchantService;

    @GetMapping
    public ApiResponse<PageResponse<MerchantDTO>> getMerchants(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MerchantStatus status) {
        
        PageResponse<MerchantDTO> result = merchantService.getMerchants(
                page, pageSize, keyword, status
        );
        return ApiResponse.success(result);
    }

    @PostMapping("/status")
    public ApiResponse<MerchantDTO> updateStatus(@RequestBody UpdateStatusRequest request) {
        MerchantDTO result = merchantService.updateStatus(request.getId(), request.getStatus());
        return ApiResponse.success(result);
    }

    @Data
    public static class UpdateStatusRequest {
        private String id;
        private MerchantStatus status;
    }
}
