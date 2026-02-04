package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.ProductDTO;
import io.cjc.backend.dto.ProductDetailDTO;
import io.cjc.backend.enums.ProductStatus;
import io.cjc.backend.service.ProductService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailDTO> getProductById(@PathVariable String id) {
        ProductDetailDTO result = productService.getProductById(id);
        return ApiResponse.success(result);
    }

    @Data
    public static class UpdateStatusRequest {
        private String id;
        private ProductStatus status;
    }
}
