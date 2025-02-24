package com.soli.frankit.dto;

import com.soli.frankit.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "상품 응답 DTO")
public class ProductResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "아이폰 15 pro")
    private String name;

    @Schema(description = "상품 설명", example = "최신형 애플 스마트폰입니다.")
    private String description;

    @Schema(description = "가격", example = "1000000")
    private BigDecimal price;

    @Schema(description = "배송비", example = "3000")
    private BigDecimal shippingFee;

    @Schema(description = "상품 활성화 여부", example = "true")
    private boolean isActive;

    @Schema(description = "등록일", example = "2025-02-24T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "수정일", example = "2025-02-25T12:34:56")
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
                                .isActive(product.isActive())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())
                                .build();
    }
}
