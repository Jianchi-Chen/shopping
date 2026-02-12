package io.cjc.backend.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private String id;
    private String productId;
    private String orderId;
    private String reviewerId;
    private String reviewerName;
    private Integer rating;
    private String content;
    private String createdAt;
}
