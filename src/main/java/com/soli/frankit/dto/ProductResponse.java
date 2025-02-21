package com.soli.frankit.dto;

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
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
