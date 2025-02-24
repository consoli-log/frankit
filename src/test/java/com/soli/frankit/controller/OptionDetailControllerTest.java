package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.TestSecurityConfig;
import com.soli.frankit.dto.OptionDetailRequest;
import com.soli.frankit.dto.OptionDetailResponse;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.service.OptionDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
 * fileName     : OptionDetailControllerTest
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : OptionDetailController의 상세 옵션 관리 API 테스트
 */
@WebMvcTest(OptionDetailController.class)
@Import(TestSecurityConfig.class)
class OptionDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OptionDetailService optionDetailService;

    private Long optionId;
    private Long detailId;
    private Long invalidId;

    private OptionDetailRequest validRequest;
    private OptionDetailRequest blankNameRequest;
    private OptionDetailRequest negativePriceRequest;

    private OptionDetailResponse detailResponse;

    @BeforeEach
    void setUp() {
        optionId = 1L;
        detailId = 100L;
        invalidId = 100L;

        validRequest = new OptionDetailRequest("테스트 상세 옵션", BigDecimal.valueOf(2000));
        blankNameRequest = new OptionDetailRequest("", BigDecimal.valueOf(2000));
        negativePriceRequest = new OptionDetailRequest("테스트 상세 옵션", BigDecimal.valueOf(-2000));

        detailResponse = OptionDetailResponse.builder()
                                            .id(detailId)
                                            .detailName("테스트 상세 옵션")
                                            .detailPrice(BigDecimal.valueOf(2000))
                                            .isActive(true)
                                            .build();
    }

    @Test
    @DisplayName("상세 옵션 등록 성공 (200)")
    void createOptionDetailSuccess() throws Exception {
        // Given
        when(optionDetailService.createOptionDetail(any(), any())).thenReturn(detailResponse);

        // When & Then
        mockMvc.perform(post("/api/option-details/options/{optionId}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(detailId))
                .andExpect(jsonPath("$.detailName").value(validRequest.getDetailName()));

        verify(optionDetailService, times(1)).createOptionDetail(any(), any());
    }

    @Test
    @DisplayName("상세 옵션 등록 실패 - 상세 옵션명 공백 (400)")
    void createOptionDetailFail_BlankName() throws Exception {
        mockMvc.perform(post("/api/option-details/options/{optionId}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankNameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detailName").value("상세 옵션명은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("상세 옵션 등록 실패 - 상세 옵션 추가 금액이 음수 (400)")
    void createOptionDetailFail_NegativePrice() throws Exception {
        mockMvc.perform(post("/api/option-details/options/{optionId}", optionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(negativePriceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detailPrice").value("상세 옵션 추가 금액은 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상세 옵션 등록 실패 - 옵션이 존재하지 않음 (404)")
    void createOptionDetailFail_OptionNotFound() throws Exception {
        // Given
        when(optionDetailService.createOptionDetail(any(), any()))
                .thenThrow(new CustomException(ErrorCode.OPTION_NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/api/option-details/options/{optionId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상세 옵션 수정 성공 (200)")
    void updateOptionDetailSuccess() throws Exception {
        // Given
        when(optionDetailService.updateOptionDetail(any(), any())).thenReturn(detailResponse);

        // When & Then
        mockMvc.perform(put("/api/option-details/{detailId}", detailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailName").value(validRequest.getDetailName()));

        verify(optionDetailService, times(1)).updateOptionDetail(any(), any());
    }

    @Test
    @DisplayName("상세 옵션 수정 실패 - 상세 옵션명 공백 (400)")
    void updateOptionDetailFail_BlankName() throws Exception {
        mockMvc.perform(put("/api/option-details/{detailId}", detailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankNameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detailName").value("상세 옵션명은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("상세 옵션 수정 실패 - 상세 옵션 추가 금액이 음수 (400)")
    void updateOptionDetailFail_NegativePrice() throws Exception {
        mockMvc.perform(put("/api/option-details/{detailId}", detailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(negativePriceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detailPrice").value("상세 옵션 추가 금액은 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상세 옵션 수정 실패 - 주문된 상세 옵션 (409)")
    void updateOptionDetailFail_HasDetailOrders() throws Exception {
        // Given
        when(optionDetailService.updateOptionDetail(any(), any()))
                .thenThrow(new CustomException(ErrorCode.OPTION_DETAIL_CANNOT_BE_UPDATED));

        // When & Then
        mockMvc.perform(put("/api/option-details/{detailId}", detailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_DETAIL_CANNOT_BE_UPDATED.getMessage()));

    }

    @Test
    @DisplayName("상세 옵션 수정 실패 - 상세 옵션이 존재하지 않음 (404)")
    void updateOptionDetailFail_DetailNotFound() throws Exception {
        // Given
        when(optionDetailService.updateOptionDetail(any(), any()))
                .thenThrow(new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND));

        // When & Then
        mockMvc.perform(put("/api/option-details/{detailId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상세 옵션 삭제 성공 (204)")
    void deleteOptionDetailSuccess() throws Exception {
        mockMvc.perform(delete("/api/option-details/{detailId}", detailId))
                .andExpect(status().isNoContent());

        verify(optionDetailService, times(1)).deleteOptionDetail(detailId);
    }

    @Test
    @DisplayName("상세 옵션 삭제 실패 - 주문된 상세 옵션 (409)")
    void deleteOptionDetailFail_HasDetailOrders() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_DETAIL_CANNOT_BE_DELETED))
                .when(optionDetailService).deleteOptionDetail(detailId);

        // When & Then
        mockMvc.perform(delete("/api/option-details/{detailId}", detailId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_DETAIL_CANNOT_BE_DELETED.getMessage()));
    }

    @Test
    @DisplayName("상세 옵션 삭제 실패 - 상세 옵션이 존재하지 않음 (404)")
    void deleteOptionDetailFail_DetailNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND))
                .when(optionDetailService).deleteOptionDetail(invalidId);

        // When & Then
        mockMvc.perform(delete("/api/option-details/{detailId}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상세 옵션 활성화 성공 (204)")
    void activateOptionDetailSuccess() throws Exception {
        mockMvc.perform(put("/api/option-details/{detailId}/activate", detailId))
                .andExpect(status().isNoContent());

        verify(optionDetailService, times(1)).activateDetail(detailId);
    }

    @Test
    @DisplayName("상세 옵션 활성화 실패 - 상세 옵션이 존재하지 않음 (404)")
    void activateOptionDetailFail_DetailNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND))
                .when(optionDetailService).activateDetail(invalidId);

        // When & Then
        mockMvc.perform(put("/api/option-details/{detailId}/activate", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상세 옵션 비활성화 성공 (204)")
    void deactivateOptionDetailSuccess() throws Exception {
        mockMvc.perform(put("/api/option-details/{detailId}/deactivate", detailId))
                .andExpect(status().isNoContent());

        verify(optionDetailService, times(1)).deactivateDetail(detailId);
    }

    @Test
    @DisplayName("상세 옵션 비활성화 실패 - 상세 옵션이 존재하지 않음 (404)")
    void deactivateOptionDetailFail_DetailNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND))
                .when(optionDetailService).deactivateDetail(invalidId);

        // When & Then
        mockMvc.perform(put("/api/option-details/{detailId}/deactivate", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("특정 옵션의 모든 상세 옵션 조회 성공 - 옵션이 있는 경우 (200)")
    void getAllDetailsByOptionSuccess() throws Exception {
        // Given
        when(optionDetailService.getAllDetailsByOption(any())).thenReturn(List.of(detailResponse));

        // When & Then
        mockMvc.perform(get("/api/option-details/options/{optionId}", optionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].detailName").value(detailResponse.getDetailName()));

        verify(optionDetailService, times(1)).getAllDetailsByOption(any());
    }

    @Test
    @DisplayName("특정 옵션의 모든 상세 옵션 조회 성공 - 옵션이 없는 경우 (200)")
    void getAllDetailsByOptionSuccess_Empty() throws Exception {
        // Given
        when(optionDetailService.getAllDetailsByOption(any())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/option-details/options/{optionId}", optionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("특정 옵션의 활성화된 상세 옵션 조회 성공 - 활성화된 옵션이 있는 경우 (200)")
    void getActiveDetailsByOptionSuccess() throws Exception {
        // Given
        when(optionDetailService.getActiveDetailsByOption(any())).thenReturn(List.of(detailResponse));

        // When & Then
        mockMvc.perform(get("/api/option-details/options/{optionId}/active", optionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].detailName").value(detailResponse.getDetailName()));

        verify(optionDetailService, times(1)).getActiveDetailsByOption(any());
    }

    @Test
    @DisplayName("특정 옵션의 활성화된 상세 옵션 조회 성공 - 활성화된 옵션이 없는 경우 (200)")
    void getActiveDetailsByOptionSuccess_Empty() throws Exception {
        // Given
        when(optionDetailService.getActiveDetailsByOption(any())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/option-details/options/{optionId}/active", optionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}