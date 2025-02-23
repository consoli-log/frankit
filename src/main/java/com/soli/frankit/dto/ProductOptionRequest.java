package com.soli.frankit.dto;

import com.soli.frankit.entity.OptionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * packageName  : com.soli.frankit.dto
 * fileName     : ProductOptionRequest
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 옵션 등록/수정 요청을 처리하는 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionRequest {

    @NotBlank(message = "옵션명은 필수 입력값입니다.")
    private String optionName;

    @NotNull(message = "옵션 타입은 필수 입력값입니다.")
    private OptionType optionType;

    @Min(value = 0, message = "옵션 추가 금액은 0원 이상이어야 합니다.")
    private BigDecimal optionPrice;
}
