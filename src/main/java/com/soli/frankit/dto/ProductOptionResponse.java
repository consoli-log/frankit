package com.soli.frankit.dto;

import com.soli.frankit.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * packageName  : com.soli.frankit.dto
 * fileName     : ProductOptionResponse
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 옵션 응답 DTO
 */

@Getter
@AllArgsConstructor
@Builder
public class ProductOptionResponse {

    private Long id;
    private String optionName;
    private String optionType;
    private BigDecimal optionPrice;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * ProductOption 엔티티를 ProductOptionResponse DTO로 변환
     *
     * @param option 상품 옵션 엔티티
     * @return ProductOptionResponse DTO
     */
    public static ProductOptionResponse from(ProductOption option) {
        return ProductOptionResponse.builder()
                .id(option.getId())
                .optionName(option.getOptionName())
                .optionType(option.getOptionType().name())
                .optionPrice(option.getOptionPrice())
                .isActive(option.isActive())
                .build();
    }

}
