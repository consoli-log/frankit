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
 * packageName  : com.soli.frankit.dto
 * fileName     : OptionDetailRequest
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상세 옵션 등록/수정 요청을 처리하는 DTO
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상세 옵션 등록 및 수정 요청 DTO")
public class OptionDetailRequest {

    @Schema(description = "상세 옵션명", example = "사이즈 추가")
    @NotBlank(message = "상세 옵션명은 필수 입력값입니다.")
    private String detailName;

    @Schema(description = "상세 옵션 추가 금액", example = "5000")
    @NotNull(message = "상세 옵션 추가 금액은 필수 입력값입니다.")
    @Min(value = 0, message = "상세 옵션 추가 금액은 0원 이상이어야 합니다.")
    private BigDecimal detailPrice;
}
