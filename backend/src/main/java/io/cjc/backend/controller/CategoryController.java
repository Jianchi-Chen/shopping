package io.cjc.backend.controller;

import io.cjc.backend.common.ApiResponse;
import io.cjc.backend.dto.CategoryDTO;
import io.cjc.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commerce/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> result = categoryService.getAllCategories();
        return ApiResponse.success(result);
    }
}
