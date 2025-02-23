package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.TestSecurityConfig;
import com.soli.frankit.dto.ProductOptionRequest;
import com.soli.frankit.dto.ProductOptionResponse;
import com.soli.frankit.entity.OptionType;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.service.ProductOptionService;
import org.hamcrest.Matchers;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName  : com.soli.frankit.controller
 * fileName     : ProductOptionControllerTest
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : ProductOptionController의 옵션 관리 API 테스트
 */
@WebMvcTest(ProductOptionController.class)
@Import(TestSecurityConfig.class)
class ProductOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductOptionService productOptionService;

    private Long productId;
    private Long optionId;
    private Long invalidId;

    private ProductOptionRequest validCreateRequest;
    private ProductOptionRequest validUpdateRequest;
    private ProductOptionRequest changedTypeRequest;

    private ProductOptionResponse validCreateResponse;
    private ProductOptionResponse validUpdateResponse;
    private ProductOptionResponse changedTypeResponse;

    @BeforeEach
    void setUp() {
        productId = 1L;
        optionId = 10L;
        invalidId = 999L;

        validCreateRequest = new ProductOptionRequest("테스트 옵션", OptionType.INPUT, BigDecimal.valueOf(2000));
        validUpdateRequest = new ProductOptionRequest("수정된 옵션", OptionType.INPUT, BigDecimal.valueOf(1500));
        changedTypeRequest = new ProductOptionRequest("변경된 옵션", OptionType.SELECT, null);

        validCreateResponse = ProductOptionResponse.builder()
                                                    .id(optionId)
                                                    .optionName("테스트 옵션")
                                                    .optionType(OptionType.INPUT)
                                                    .optionPrice(BigDecimal.valueOf(2000))
                                                    .isActive(true)
                                                    .build();

        validUpdateResponse = ProductOptionResponse.builder()
                                                    .id(optionId)
                                                    .optionName("수정된 옵션")
                                                    .optionType(OptionType.INPUT)
                                                    .optionPrice(BigDecimal.valueOf(1500))
                                                    .isActive(true)
                                                    .build();

        changedTypeResponse = ProductOptionResponse.builder()
                                                    .id(optionId)
                                                    .optionName("변경된 옵션")
                                                    .optionType(OptionType.SELECT)
                                                    .optionPrice(null)
                                                    .isActive(true)
                                                    .build();
    }

    @Test
    @DisplayName("옵션 등록 성공")
    void createProductOptionSuccess() throws Exception {
        // Given
        when(productOptionService.createProductOption(any(), any())).thenReturn(validCreateResponse);

        // When & Then
        mockMvc.perform(post("/api/product-options/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(optionId))
                .andExpect(jsonPath("$.optionName").value(validCreateRequest.getOptionName()));

        verify(productOptionService, times(1)).createProductOption(any(), any());
    }

    @Test
    @DisplayName("옵션 등록 실패 - 활성화된 옵션 3개 초과 (400)")
    void createProductOptionFail_TooManyActiveOptions() throws Exception {
        // Given
        when(productOptionService.createProductOption(any(), any())).thenThrow(new CustomException(ErrorCode.OPTION_LIMIT_EXCEEDED));

        // When & Then
        mockMvc.perform(post("/api/product-options/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_LIMIT_EXCEEDED.getMessage()));
    }

    @Test
    @DisplayName("옵션 등록 실패 - 존재하지 않는 상품 (404)")
    void createProductOptionFail_ProductNotFound() throws Exception {
        // Given
        when(productOptionService.createProductOption(any(), any()))
                .thenThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/api/product-options/products/{productId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validCreateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("옵션 수정 성공 (200)")
    void updateProductOptionSuccess() throws Exception {
        // Given
        when(productOptionService.updateProductOption(any(), any())).thenReturn(validUpdateResponse);

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionName").value("수정된 옵션"))
                .andExpect(jsonPath("$.optionType").value(OptionType.INPUT.name()))
                .andExpect(jsonPath("$.optionPrice").value(BigDecimal.valueOf(1500)));
    }

    @Test
    @DisplayName("옵션 수정 성공 - 옵션 타입 변경 (200)")
    void updateProductOptionSuccess_TypeChange() throws Exception {
        // Given
        when(productOptionService.updateProductOption(any(), any())).thenReturn(changedTypeResponse);

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changedTypeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionName").value("변경된 옵션"))
                .andExpect(jsonPath("$.optionType").value(OptionType.SELECT.name()))
                .andExpect(jsonPath("$.optionPrice").doesNotExist())
                .andExpect(jsonPath("$.optionPrice").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("옵션 수정 실패 - 주문된 옵션 (400)")
    void updateProductOptionFail_HasOptionOrders() throws Exception {
        // Given
        when(productOptionService.updateProductOption(any(), any()))
                .thenThrow(new CustomException(ErrorCode.OPTION_CANNOT_BE_UPDATED));

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_CANNOT_BE_UPDATED.getMessage()));
    }

    @Test
    @DisplayName("옵션 수정 실패 - 옵션이 존재하지 않음 (404)")
    void updateProductOptionFail_OptionNotFound() throws Exception {
        // Given
        when(productOptionService.updateProductOption(any(), any()))
                .thenThrow(new CustomException(ErrorCode.OPTION_NOT_FOUND));

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("옵션 삭제 성공 (204)")
    void deleteProductOptionSuccess() throws Exception {
        mockMvc.perform(delete("/api/product-options/{optionId}", optionId))
                .andExpect(status().isNoContent());

        verify(productOptionService, times(1)).deleteProductOption(optionId);
    }

    @Test
    @DisplayName("옵션 삭제 실패 - 주문된 옵션 (400)")
    void deleteProductOptionFail_HasOptionOrders() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_CANNOT_BE_DELETED))
                .when(productOptionService).deleteProductOption(optionId);

        // When & Then
        mockMvc.perform(delete("/api/product-options/{optionId}", optionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_CANNOT_BE_DELETED.getMessage()));
    }

    @Test
    @DisplayName("옵션 삭제 실패 - 옵션이 존재하지 않음 (404)")
    void deleteProductOptionFail_OptionNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_NOT_FOUND))
                .when(productOptionService).deleteProductOption(invalidId);

        // When & Then
        mockMvc.perform(delete("/api/product-options/{optionId}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("옵션 활성화 성공 (204)")
    void activateProductOptionSuccess() throws Exception {
        mockMvc.perform(put("/api/product-options/{optionId}/activate", optionId))
                .andExpect(status().isNoContent());

        verify(productOptionService, times(1)).activateOption(optionId);
    }

    @Test
    @DisplayName("옵션 활성화 실패 - 활성화된 옵션 3개 초과 (400)")
    void activateProductOptionFail_TooManyActiveOptions() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_LIMIT_EXCEEDED))
                .when(productOptionService).activateOption(optionId);

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}/activate", optionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_LIMIT_EXCEEDED.getMessage()));
    }

    @Test
    @DisplayName("옵션 활성화 실패 - 옵션이 존재하지 않음 (404)")
    void activateProductOptionFail_OptionNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_NOT_FOUND))
                .when(productOptionService).activateOption(invalidId);

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}/activate", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("옵션 비활성화 성공 (204)")
    void deactivateProductOptionSuccess() throws Exception {
        mockMvc.perform(put("/api/product-options/{optionId}/deactivate", optionId))
                .andExpect(status().isNoContent());

        verify(productOptionService, times(1)).deactivateOption(optionId);
    }

    @Test
    @DisplayName("옵션 비활성화 실패 - 옵션이 존재하지 않음 (404)")
    void deactivateProductOptionFail_OptionNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_NOT_FOUND))
                .when(productOptionService).deactivateOption(invalidId);

        // When & Then
        mockMvc.perform(put("/api/product-options/{optionId}/deactivate", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("특정 상품의 모든 옵션 조회 성공 - 옵션이 있는 경우 (200)")
    void getAllOptionsByProductSuccess() throws Exception {
        // Given
        when(productOptionService.getAllOptionsByProduct(any())).thenReturn(List.of(validCreateResponse));

        // When & Then
        mockMvc.perform(get("/api/product-options/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].optionName").value(validCreateResponse.getOptionName()));

        verify(productOptionService, times(1)).getAllOptionsByProduct(any());
    }

    @Test
    @DisplayName("특정 상품의 모든 옵션 조회 성공 - 옵션이 없는 경우 (200)")
    void getAllOptionsByProductSuccess_Empty() throws Exception {
        // Given
        when(productOptionService.getAllOptionsByProduct(any())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/product-options/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("상품의 활성화된 옵션 조회 성공 - 활성화된 옵션이 있는 경우 (200)")
    void getActiveOptionsByProductSuccess() throws Exception {
        // Given
        when(productOptionService.getActiveOptionsByProduct(any())).thenReturn(List.of(validCreateResponse));

        // When & Then
        mockMvc.perform(get("/api/product-options/products/{productId}/active", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].optionName").value(validCreateResponse.getOptionName()));

        verify(productOptionService, times(1)).getActiveOptionsByProduct(any());
    }

    @Test
    @DisplayName("특정 상품의 활성화된 옵션 조회 성공 - 활성화된 옵션이 없는 경우 (200)")
    void getActiveOptionsByProductSuccess_Empty() throws Exception {
        // Given
        when(productOptionService.getActiveOptionsByProduct(any())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/product-options/products/{productId}/active", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}