package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.TestSecurityConfig;
import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName  : com.soli.frankit.controller
 * fileName     : ProductControllerTest
 * author       : eumsoli
 * date         : 2025-02-21
 * description  : ProductController의 상품관리 API 테스트
 */
@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private Long vaildId;
    private Long invaildId;

    private ProductRequest validRequest;
    private ProductRequest blankNameRequest;
    private ProductRequest negativePriceRequest;

    private ProductRequest validUpdateRequest;
    private ProductRequest blankDescriptionRequest;
    private ProductRequest negativePriceUpdateRequest;

    private ProductResponse validResponse;
    private ProductResponse validUpdateResponse;

    @BeforeEach
    void setUp() {
        vaildId = 1L;
        invaildId = 999L;

        validRequest = new ProductRequest("상품명", "상품 설명", BigDecimal.valueOf(20000));
        blankNameRequest = new ProductRequest("", "상품 설명", BigDecimal.valueOf(20000));
        negativePriceRequest = new ProductRequest("상품명", "상품 설명", BigDecimal.valueOf(-20000));

        validUpdateRequest = new ProductRequest("상품명 수정", "상품 설명 수정", BigDecimal.valueOf(50000));
        blankDescriptionRequest = new ProductRequest("상품명 수정", "", BigDecimal.valueOf(50000));
        negativePriceUpdateRequest = new ProductRequest("상품명 수정", "상품 설명 수정", BigDecimal.valueOf(-50000));

        validResponse = ProductResponse.builder()
                                            .id(vaildId)
                                            .name("상품명")
                                            .description("상품 설명")
                                            .price(BigDecimal.valueOf(20000))
                                            .build();

        validUpdateResponse = ProductResponse.builder()
                                                .id(vaildId)
                                                .name("상품명 수정")
                                                .description("상품 설명 수정")
                                                .price(BigDecimal.valueOf(50000))
                                                .build();
    }

    @Test
    @DisplayName("상품 등록 성공 (200)")
    void createProductSuccess() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(validResponse);

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상품명"))
                .andExpect(jsonPath("$.description").value("상품 설명"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(20000)));
    }

    @Test
    @DisplayName("상품 등록 실패 - 상품명 공백 (400)")
    void createProductFail_BlankName() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankNameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("상품명은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("상품 등록 실패 - 가격이 음수 (400)")
    void createProductFail_NegativePrice() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(negativePriceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("가격은 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상품 수정 성공 (200)")
    void updateProductSuccess() throws Exception {
        // Given
        when(productService.updateProduct(any(Long.class), any(ProductRequest.class))).thenReturn(validUpdateResponse);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", vaildId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상품명 수정"))
                .andExpect(jsonPath("$.description").value("상품 설명 수정"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(50000)));
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품이 존재하지 않음 (404)")
    void updateProductFail_ProductNotFound() throws Exception {
        // given
        doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).updateProduct(eq(invaildId), any(ProductRequest.class));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", invaildId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품 설명 공백 (400)")
    void updateProductFail_BlankDescription() throws Exception {
        mockMvc.perform(put("/api/products/{id}", vaildId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankDescriptionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("상품 설명은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("상품 수정 실패 - 가격이 음수 (400)")
    void updateProductFail_NegativePrice() throws Exception {
        mockMvc.perform(put("/api/products/{id}", vaildId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(negativePriceUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("가격은 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상품 삭제 성공 (200)")
    void deleteProductSuccess() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", vaildId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품이 존재하지 않음 (404)")
    void deleteProductFail_ProductNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).deleteProduct(eq(invaildId));

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", invaildId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

}