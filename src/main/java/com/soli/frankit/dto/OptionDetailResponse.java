package com.soli.frankit.dto;

import com.soli.frankit.entity.OptionDetail;
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
public class OptionDetailResponse {

    private Long id;
    private String detailName;
    private BigDecimal detailPrice;
    private boolean isActive;
    private LocalDateTime createdAt;
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
