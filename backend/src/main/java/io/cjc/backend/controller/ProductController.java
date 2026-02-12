package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.CreateProductRequest;
import io.cjc.backend.dto.ProductDTO;
import io.cjc.backend.dto.ProductDetailDTO;
import io.cjc.backend.enums.ProductStatus;
import io.cjc.backend.security.UserPrincipal;
import io.cjc.backend.service.ProductService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commerce/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @GetMapping
    public ApiResponse<PageResponse<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String shopId) {
        
        PageResponse<ProductDTO> result = productService.getProducts(
                page, pageSize, keyword, status, category, shopId
        );
        return ApiResponse.success(result);
    }

    @PostMapping("/status")
    public ApiResponse<ProductDTO> updateStatus(@RequestBody UpdateStatusRequest request) {
        ProductDTO result = productService.updateStatus(request.getId(), request.getStatus());
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<ProductDTO> createProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateProductRequest request) {
        // 如果是商家，自动填充 shopId
        if (principal != null && "MERCHANT".equals(principal.getRole())) {
            String merchantId = principal.getMerchantId();
            if (merchantId != null && !merchantId.isEmpty()) {
                request.setShopId(merchantId);
            }
        }
        
        // 验证必填字段
        if (request.getShopId() == null || request.getShopId().isEmpty()) {
            return ApiResponse.error(400, "缺少商家信息，无法新增商品");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            return ApiResponse.error(400, "商品名称不能为空");
        }
        if (request.getTitle().length() > 255) {
            return ApiResponse.error(400, "商品名称不能超过255个字符");
        }
        if (request.getSku() == null || request.getSku().isEmpty()) {
            return ApiResponse.error(400, "SKU不能为空");
        }
        if (request.getSku().length() > 100) {
            return ApiResponse.error(400, "SKU不能超过100个字符");
        }
        if (request.getPrice() == null || request.getPrice().signum() <= 0) {
            return ApiResponse.error(400, "价格必须大于0");
        }
        
        ProductDTO result = productService.createProduct(request);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDTO> updateProduct(
            @PathVariable String id,
            @RequestBody CreateProductRequest request) {
        // 验证必填字段
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            return ApiResponse.error(400, "商品名称不能为空");
        }
        if (request.getTitle().length() > 255) {
            return ApiResponse.error(400, "商品名称不能超过255个字符");
        }
        if (request.getSku() == null || request.getSku().isEmpty()) {
            return ApiResponse.error(400, "SKU不能为空");
        }
        if (request.getSku().length() > 100) {
            return ApiResponse.error(400, "SKU不能超过100个字符");
        }
        if (request.getPrice() == null || request.getPrice().signum() <= 0) {
            return ApiResponse.error(400, "价格必须大于0");
        }
        
        ProductDTO result = productService.updateProduct(id, request);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailDTO> getProductById(@PathVariable String id) {
        ProductDetailDTO result = productService.getProductById(id);
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/view")
    public ApiResponse<Void> recordView(@PathVariable String id) {
        productService.recordProductView(id);
        return ApiResponse.success(null);
    }

    @Data
    public static class UpdateStatusRequest {
        private String id;
        private ProductStatus status;
    }

}
