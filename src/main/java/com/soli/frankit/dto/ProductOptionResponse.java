package com.soli.frankit.dto;

import com.soli.frankit.entity.OptionType;
import com.soli.frankit.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "상품 옵션 응답 DTO")
public class ProductOptionResponse {

    @Schema(description = "상품 옵션 ID", example = "1")
    private Long id;

    @Schema(description = "옵션명", example = "색상 선택")
    private String optionName;

    @Schema(description = "옵션 타입", example = "INPUT (입력형) 또는 SELECT (선택형)")
    private OptionType optionType;

    @Schema(description = "옵션 추가 금액 (입력형 옵션만 해당)", example = "5000")
    private BigDecimal optionPrice;

    @Schema(description = "옵션 활성화 여부", example = "true")
    private boolean isActive;

    @Schema(description = "옵션 등록일", example = "2025-02-24T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "옵션 수정일", example = "2025-02-25T12:34:56")
    private LocalDateTime updatedAt;

    /**
     * ProductOption 엔티티를 ProductOptionResponse DTO로 변환
     *
     * @param option 변환할 상품 옵션 엔티티
     * @return ProductOptionResponse DTO
     */
    public static ProductOptionResponse from(ProductOption option) {
        return ProductOptionResponse.builder()
                .id(option.getId())
                .optionName(option.getOptionName())
                .optionType(option.getOptionType())
                .optionPrice(option.getOptionPrice())
                .isActive(option.isActive())
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .build();
    }

}
