package com.soli.frankit.dto;

import com.soli.frankit.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * packageName  com.soli.frankit.dto
 * fileName     ProductResponse
 * author       eumsoli
 * date         2025-02-20
 * description  상품 응답 DTO
 */
@Getter
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal shippingFee;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Product 엔티티를 ProductResponse DTO로 변환
     *
     * @param product 변환할 상품 엔티티
     * @return ProductResponse DTO
     */
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .shippingFee(product.getShippingFee())
                .isActive(product.isActive()) // 활성화 상태 추가
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
