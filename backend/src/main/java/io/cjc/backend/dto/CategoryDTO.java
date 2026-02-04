package io.cjc.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private String id;
    private String name;
    private String icon;
    private Integer productCount;
    private List<CategoryDTO> children;
}
