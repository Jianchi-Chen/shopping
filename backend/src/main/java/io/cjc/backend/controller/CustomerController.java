package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.CustomerDTO;
import io.cjc.backend.enums.CustomerStatus;
import io.cjc.backend.service.CustomerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity/users")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;

    @GetMapping
    public ApiResponse<PageResponse<CustomerDTO>> getCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CustomerStatus status) {
        
        PageResponse<CustomerDTO> result = customerService.getCustomers(
                page, pageSize, keyword, status
        );
        return ApiResponse.success(result);
    }

    @PostMapping("/status")
    public ApiResponse<CustomerDTO> updateStatus(@RequestBody UpdateStatusRequest request) {
        CustomerDTO result = customerService.updateStatus(request.getId(), request.getStatus());
        return ApiResponse.success(result);
    }

    @Data
    public static class UpdateStatusRequest {
        private String id;
        private CustomerStatus status;
    }
}
