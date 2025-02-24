package com.soli.frankit.service;

import com.soli.frankit.config.TestEnvConfig;
import com.soli.frankit.dto.OptionDetailRequest;
import com.soli.frankit.dto.OptionDetailResponse;
import com.soli.frankit.entity.OptionDetail;
import com.soli.frankit.entity.OptionType;
import com.soli.frankit.entity.ProductOption;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.OptionDetailRepository;
import com.soli.frankit.repository.ProductOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * packageName  : com.soli.frankit.service
 * fileName     : OptionDetailServiceTest
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : OptionDetailService의 상품 상세 옵션 관리 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
@Import(TestEnvConfig.class)
class OptionDetailServiceTest {

    @InjectMocks
    private OptionDetailService optionDetailService;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private OptionDetailRepository optionDetailRepository;

    @Mock
    private OrderService orderService;

    private Long optionId;
    private Long detailId;
    private Long invalidId;

    private ProductOption productOption;
    private OptionDetail validDetail;
    private OptionDetail inactiveDetail;

    private OptionDetailRequest validCreateRequest;
    private OptionDetailRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        optionId = 1L;
        detailId = 10L;
        invalidId = 999L;

        productOption = ProductOption.builder()
                                    .optionName("선택형 옵션")
                                    .optionType(OptionType.SELECT)
                                    .build();

        validDetail = OptionDetail.builder()
                                    .productOption(productOption)
                                    .detailName("테스트 상세 옵션")
                                    .detailPrice(BigDecimal.valueOf(1000))
                                    .build();

        inactiveDetail = OptionDetail.builder()
                                    .productOption(productOption)
                                    .detailName("비활성화 상세 옵션")
                                    .detailPrice(BigDecimal.valueOf(1500))
                                    .build();
        inactiveDetail.deactivate();

        validCreateRequest = new OptionDetailRequest("테스트 상세 옵션", BigDecimal.valueOf(1000));
        validUpdateRequest = new OptionDetailRequest("수정된 상세 옵션", BigDecimal.valueOf(2000));
    }

    @Test
    @DisplayName("상세 옵션 등록 성공")
    void createOptionDetailSuccess() {
        // Given
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(productOption));
        when(optionDetailRepository.save(any(OptionDetail.class))).thenReturn(validDetail);

        // When
        OptionDetailResponse response = optionDetailService.createOptionDetail(optionId, validCreateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDetailName()).isEqualTo(validCreateRequest.getDetailName());
        verify(optionDetailRepository, times(1)).save(any(OptionDetail.class));
    }

    @Test
    @DisplayName("상세 옵션 등록 실패 - 옵션이 존재하지 않음")
    void createOptionDetailFail_OptionNotFound() {
        // Given
        when(productOptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> optionDetailService.createOptionDetail(invalidId, validCreateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상세 옵션 수정 성공")
    void updateOptionDetailSuccess() {
        // Given
        when(orderService.hasDetailOrders(detailId)).thenReturn(false);
        when(optionDetailRepository.findById(detailId)).thenReturn(Optional.of(validDetail));

        // When
        OptionDetailResponse response = optionDetailService.updateOptionDetail(detailId, validUpdateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDetailName()).isEqualTo(validUpdateRequest.getDetailName());
    }

    @Test
    @DisplayName("상세 옵션 수정 실패 - 주문된 옵션")
    void updateOptionDetailFail_HasDetailOrders() {
        // Given
        when(orderService.hasDetailOrders(detailId)).thenReturn(true);
        when(optionDetailRepository.findById(detailId)).thenReturn(Optional.of(validDetail));

        // When & Then
        assertThatThrownBy(() -> optionDetailService.updateOptionDetail(detailId, validUpdateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_DETAIL_CANNOT_BE_UPDATED.getMessage());
    }

    @Test
    @DisplayName("상세 옵션 수정 실패 - 상세 옵션이 존재하지 않음")
    void updateOptionDetailFail_DetailNotFound() {
        // Given
        when(optionDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> optionDetailService.updateOptionDetail(invalidId, validUpdateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상세 옵션 삭제 성공")
    void deleteOptionDetailSuccess() {
        // Given
        when(orderService.hasDetailOrders(detailId)).thenReturn(false);
        when(optionDetailRepository.findById(detailId)).thenReturn(Optional.of(validDetail));

        // When
        optionDetailService.deleteOptionDetail(detailId);

        // Then
        verify(optionDetailRepository, times(1)).delete(validDetail);
    }

    @Test
    @DisplayName("상세 옵션 삭제 실패 - 주문된 상세 옵션")
    void deleteOptionDetailFail_HasDetailOrders() {
        // Given
        when(optionDetailRepository.findById(detailId)).thenReturn(Optional.of(validDetail));
        when(orderService.hasDetailOrders(detailId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> optionDetailService.deleteOptionDetail(detailId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_DETAIL_CANNOT_BE_DELETED.getMessage());
    }

    @Test
    @DisplayName("상세 옵션 삭제 실패 - 상세 옵션이 존재하지 않음")
    void deleteOptionDetailFail_DetailNotFound() {
        // Given
        when(optionDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> optionDetailService.deleteOptionDetail(invalidId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상세 옵션 활성화 성공")
    void activateOptionDetailSuccess() {
        // Given
        when(optionDetailRepository.findById(detailId)).thenReturn(Optional.of(inactiveDetail));

        // When
        optionDetailService.activateDetail(detailId);

        // Then
        assertThat(inactiveDetail.isActive()).isTrue();
    }

    @Test
    @DisplayName("상세 옵션 활성화 실패 - 상세 옵션이 존재하지 않음")
    void activateOptionDetailFail_DetailNotFound() {
        // Given
        when(optionDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> optionDetailService.activateDetail(invalidId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상세 옵션 비활성화 성공")
    void deactivateOptionDetailSuccess() {
        // Given
        when(optionDetailRepository.findById(detailId)).thenReturn(Optional.of(validDetail));

        // When
        optionDetailService.deactivateDetail(detailId);

        // Then
        assertThat(validDetail.isActive()).isFalse();
    }

    @Test
    @DisplayName("상세 옵션 비활성화 실패 - 상세 옵션이 존재하지 않음")
    void deactivateOptionDetailFail_DetailNotFound() {
        // Given
        when(optionDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> optionDetailService.deactivateDetail(invalidId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_DETAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 옵션의 모든 상세 옵션 조회 성공 - 옵션이 있는 경우")
    void getAllDetailsByOptionSuccess() {
        // Given
        when(optionDetailRepository.findByProductOptionId(optionId)).thenReturn(List.of(validDetail, inactiveDetail));

        // When
        List<OptionDetailResponse> details = optionDetailService.getAllDetailsByOption(optionId);

        // Then
        assertThat(details).hasSize(2);
    }

    @Test
    @DisplayName("특정 옵션의 모든 상세 옵션 조회 성공 - 옵션이 없는 경우")
    void getAllDetailsByOptionSuccess_Empty() {
        // Given
        when(optionDetailRepository.findByProductOptionId(optionId)).thenReturn(List.of());

        // When
        List<OptionDetailResponse> details = optionDetailService.getAllDetailsByOption(optionId);

        // Then
        assertThat(details).isEmpty();
    }

    @Test
    @DisplayName("특정 옵션의 활성화된 상세 옵션 조회 성공 - 활성화된 옵션이 있는 경우")
    void getActiveDetailsByOptionSuccess() {
        // Given
        when(optionDetailRepository.findByProductOptionIdAndIsActiveTrue(optionId)).thenReturn(List.of(validDetail));

        // When
        List<OptionDetailResponse> activeDetails = optionDetailService.getActiveDetailsByOption(optionId);

        // Then
        assertThat(activeDetails).hasSize(1);
    }

    @Test
    @DisplayName("특정 옵션의 활성화된 상세 옵션 조회 성공 - 활성화된 옵션이 없는 경우")
    void getActiveDetailsByOptionSuccess_Empty() {
        // Given
        when(optionDetailRepository.findByProductOptionIdAndIsActiveTrue(optionId)).thenReturn(List.of());

        // When
        List<OptionDetailResponse> activeDetails = optionDetailService.getActiveDetailsByOption(optionId);

        // Then
        assertThat(activeDetails).isEmpty();
    }

}