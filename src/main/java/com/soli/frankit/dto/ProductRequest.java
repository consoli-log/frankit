package com.soli.frankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * packageName  com.soli.frankit.dto
 * fileName     ProductRequest
 * author       eumsoli
 * date         2025-02-20
 * description  상품 등록/수정 요청을 처리하는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품 등록 및 수정 요청 DTO")
public class ProductRequest {

    @Schema(description = "상품명", example = "아이폰 15 pro")
    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String name;

    @Schema(description = "상품 설명", example = "최신형 애플 스마트폰입니다.")
    @NotBlank(message = "상품 설명은 필수 입력값입니다.")
    private String description;

    @Schema(description = "가격", example = "1000000")
    @NotNull(message = "가격은 필수 입력값입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private BigDecimal price;

    @Schema(description = "배송비", example = "3000")
    @NotNull(message = "배송비는 필수 입력값입니다.")
    @Min(value = 0, message = "배송비는 0원 이상이어야 합니다.")
    private BigDecimal shippingFee;

}