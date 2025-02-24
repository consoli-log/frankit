package com.soli.frankit.dto;

import com.soli.frankit.entity.OptionDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * packageName  : com.soli.frankit.dto
 * fileName     : OptionDetailResponse
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상세 옵션 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "상세 옵션 응답 DTO")
public class OptionDetailResponse {

    @Schema(description = "상세 옵션 ID", example = "1")
    private Long id;

    @Schema(description = "상세 옵션명", example = "사이즈 추가")
    private String detailName;

    @Schema(description = "상세 옵션 추가 금액", example = "5000")
    private BigDecimal detailPrice;

    @Schema(description = "옵션 활성화 여부", example = "true")
    private boolean isActive;

    @Schema(description = "상세 옵션 생성일", example = "2025-02-24T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "상세 옵션 수정일", example = "2025-02-25T12:34:56")
    private LocalDateTime updatedAt;

    /**
     * OptionDetail 엔티티를 OptionDetailResponse DTO로 변환하는 메서드
     *
     * @param detail 상세 옵션 엔티티
     * @return OptionDetailResponse DTO
     */
    public static OptionDetailResponse from(OptionDetail detail) {
        return OptionDetailResponse.builder()
                .id(detail.getId())
                .detailName(detail.getDetailName())
                .detailPrice(detail.getDetailPrice())
                .isActive(detail.isActive())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .build();
    }

}
