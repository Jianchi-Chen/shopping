package io.cjc.backend.service;

import io.cjc.backend.dto.CategoryDTO;
import io.cjc.backend.entity.Category;
import io.cjc.backend.repository.CategoryRepository;
import io.cjc.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        List<Category> rootCategories = categoryRepository.findByParentIdIsNull();
        return rootCategories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCategoryProductCounts() {
        List<Category> allCategories = categoryRepository.findAll();
        for (Category category : allCategories) {
            long count = productRepository.countByCategory(category.getName());
            category.setProductCount((int) count);
            categoryRepository.save(category);
        }
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setProductCount(category.getProductCount());
        
        // 加载子类目
        List<Category> children = categoryRepository.findByParentId(category.getId());
        if (!children.isEmpty()) {
            dto.setChildren(children.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
