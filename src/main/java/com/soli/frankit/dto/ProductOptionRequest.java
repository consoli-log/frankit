package com.soli.frankit.dto;

import com.soli.frankit.entity.OptionType;
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
@Schema(description = "상품 옵션 등록/수정 요청 DTO")
public class ProductOptionRequest {

    @NotBlank(message = "옵션명은 필수 입력값입니다.")
    @Schema(description = "옵션명", example = "색상 선택")
    private String optionName;

    @NotNull(message = "옵션 타입은 필수 입력값입니다.")
    @Schema(description = "옵션 타입", example = "INPUT 또는 SELECT")
    private OptionType optionType;

    @Min(value = 0, message = "옵션 추가 금액은 0원 이상이어야 합니다.")
    @Schema(description = "옵션 추가 금액 (입력형 옵션만 해당)", example = "5000")
    private BigDecimal optionPrice;

}
