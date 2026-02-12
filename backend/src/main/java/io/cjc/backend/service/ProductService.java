package io.cjc.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cjc.backend.common.PageResponse;
import io.cjc.backend.dto.CreateProductRequest;
import io.cjc.backend.dto.ProductDTO;
import io.cjc.backend.dto.ProductDetailDTO;
import io.cjc.backend.entity.Product;
import io.cjc.backend.enums.ProductStatus;
import io.cjc.backend.repository.ProductRepository;
import io.cjc.backend.repository.ProductViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductViewRepository productViewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> getProducts(
            Integer page, Integer pageSize,
            String keyword, ProductStatus status,
            String category, String shopId) {
        
        Pageable pageable = PageRequest.of(
                page - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );
        
        Page<Product> productPage = productRepository.findByFilters(
                keyword, status, category, shopId, pageable
        );
        
        List<ProductDTO> dtoList = productPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(page, pageSize, productPage.getTotalElements(), dtoList);
    }

    @Transactional(readOnly = true)
    public ProductDetailDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        return toDetailDTO(product);
    }

    @Transactional
    public void recordProductView(String productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return;
        }
        io.cjc.backend.entity.ProductView view = new io.cjc.backend.entity.ProductView();
        view.setProductId(productId);
        view.setShopId(product.getShopId());
        productViewRepository.save(view);
    }

    @Transactional
    public ProductDTO updateStatus(String id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setStatus(status);
        Product saved = productRepository.save(product);
        
        return toDTO(saved);
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock() == null ? 0 : request.getStock());
        product.setStatus(request.getStatus() == null ? ProductStatus.ON_SALE : request.getStatus());
        product.setCategory(request.getCategory());
        product.setShopId(request.getShopId());
        product.setShopName(request.getShopName());
        product.setDescription(request.getDescription());
        product.setImages(request.getImages());
        product.setSpecs(request.getSpecs());

        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Transactional
    public ProductDTO updateProduct(String id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setTitle(request.getTitle());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock() == null ? 0 : request.getStock());
        product.setStatus(request.getStatus() == null ? ProductStatus.ON_SALE : request.getStatus());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setImages(request.getImages());
        product.setSpecs(request.getSpecs());
        // shopId和shopName不允许修改

        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setName(product.getTitle());  // name映射自title
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setStock(product.getStock());
        dto.setStatus(product.getStatus());
        dto.setCategory(product.getCategory());
        dto.setShopId(product.getShopId());
        dto.setShopName(product.getShopName());
        dto.setImages(product.getImages());  // 添加images字段
        dto.setRating(product.getRating());  // 添加评分
        dto.setReviewCount(product.getReviewCount());  // 添加评论数
        if (product.getUpdatedAt() != null) {
            dto.setUpdatedAt(product.getUpdatedAt().format(DATE_FORMATTER));
        }
        return dto;
    }

    private ProductDetailDTO toDetailDTO(Product product) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setStock(product.getStock());
        dto.setStatus(product.getStatus());
        dto.setCategory(product.getCategory());
        dto.setShopId(product.getShopId());
        dto.setShopName(product.getShopName());
        dto.setDescription(product.getDescription());
        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());
        if (product.getCreatedAt() != null) {
            dto.setCreatedAt(product.getCreatedAt().format(DATE_FORMATTER));
        }
        if (product.getUpdatedAt() != null) {
            dto.setUpdatedAt(product.getUpdatedAt().format(DATE_FORMATTER));
        }
        
        // 解析图片列表（逗号分隔）
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setImages(Arrays.asList(product.getImages().split(",")));
        } else {
            dto.setImages(Collections.emptyList());
        }
        
        // 解析规格 JSON
        if (product.getSpecs() != null && !product.getSpecs().isEmpty()) {
            try {
                List<ProductDetailDTO.ProductSpec> specs = objectMapper.readValue(
                        product.getSpecs(),
                        new TypeReference<List<ProductDetailDTO.ProductSpec>>() {}
                );
                dto.setSpecs(specs);
            } catch (Exception e) {
                dto.setSpecs(Collections.emptyList());
            }
        } else {
            dto.setSpecs(Collections.emptyList());
        }
        
        return dto;
    }
}
