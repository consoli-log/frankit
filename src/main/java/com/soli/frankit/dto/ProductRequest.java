package com.soli.frankit.dto;

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
public class ProductRequest {

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "상품 설명은 필수 입력값입니다.")
    private String description;

    @NotNull(message = "가격은 필수 입력값입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private BigDecimal price;

    @NotNull(message = "배송비는 필수 입력값입니다.")
    @Min(value = 0, message = "배송비는 0원 이상이어야 합니다.")
    private BigDecimal shippingFee;

}