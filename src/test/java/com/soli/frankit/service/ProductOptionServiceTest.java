package com.soli.frankit.service;

import com.soli.frankit.config.TestEnvConfig;
import com.soli.frankit.dto.ProductOptionRequest;
import com.soli.frankit.dto.ProductOptionResponse;
import com.soli.frankit.entity.OptionType;
import com.soli.frankit.entity.Product;
import com.soli.frankit.entity.ProductOption;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.ProductOptionRepository;
import com.soli.frankit.repository.ProductRepository;
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
 * fileName     : ProductOptionServiceTest
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : ProductOptionService의 상품 옵션 관리 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
@Import(TestEnvConfig.class)
class ProductOptionServiceTest {

    @InjectMocks
    private ProductOptionService productOptionService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private OrderService orderService;

    private Long productId;
    private Long optionId;
    private Long invalidId;

    private Product product;
    private ProductOption optionInput;
    private ProductOption optionSelect;
    private ProductOption inactiveOption;

    private ProductOptionRequest validCreateRequest;
    private ProductOptionRequest validUpdateRequest;
    private ProductOptionRequest changedTypeRequest;

    @BeforeEach
    void setUp() {
        productId = 1L;
        optionId = 10L;
        invalidId = 999L;

        product = new Product("테스트 상품", "테스트 설명", BigDecimal.valueOf(10000), BigDecimal.valueOf(2000));

        optionInput = ProductOption.builder()
                                    .product(product)
                                    .optionName("테스트 옵션")
                                    .optionType(OptionType.INPUT)
                                    .optionPrice(BigDecimal.valueOf(2000))
                                    .build();

        optionSelect = ProductOption.builder()
                                    .product(product)
                                    .optionName("선택형 옵션")
                                    .optionType(OptionType.SELECT)
                                    .build();

        inactiveOption = ProductOption.builder()
                                        .product(product)
                                        .optionName("비활성화 옵션")
                                        .optionType(OptionType.INPUT)
                                        .optionPrice(BigDecimal.valueOf(3000))
                                        .build();
        inactiveOption.deactivate();

        validCreateRequest = new ProductOptionRequest("테스트 옵션", OptionType.INPUT, BigDecimal.valueOf(2000));
        validUpdateRequest = new ProductOptionRequest("수정된 옵션", OptionType.INPUT, BigDecimal.valueOf(1500));
        changedTypeRequest = new ProductOptionRequest("변경된 옵션", OptionType.SELECT, null);
    }

    @Test
    @DisplayName("옵션 등록 성공")
    void createProductOptionSuccess() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productOptionRepository.countByProductIdAndIsActive(productId, true)).thenReturn(2L);
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(optionInput);

        // When
        ProductOptionResponse response = productOptionService.createProductOption(productId, validCreateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOptionName()).isEqualTo(validCreateRequest.getOptionName());
        verify(productOptionRepository, times(1)).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("옵션 등록 실패 - 활성화된 옵션 3개 초과")
    void createProductOptionFail_TooManyActiveOptions() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productOptionRepository.countByProductIdAndIsActive(productId, true)).thenReturn(3L);

        // When & Then
        assertThatThrownBy(() -> productOptionService.createProductOption(productId, validCreateRequest)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("옵션 등록 실패 - 상품이 존재하지 않음")
    void createProductOptionFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productOptionService.createProductOption(invalidId, validCreateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("옵션 수정 성공")
    void updateProductOptionSuccess() {
        // Given
        when(orderService.hasOptionOrders(optionId)).thenReturn(false);
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(optionInput));

        // When
        ProductOptionResponse response = productOptionService.updateProductOption(optionId, validUpdateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOptionName()).isEqualTo(validUpdateRequest.getOptionName());
    }

    @Test
    @DisplayName("옵션 수정 성공 - 옵션 타입 변경")
    void updateProductOptionSuccess_TypeChange() {
        // Given
        when(orderService.hasOptionOrders(optionId)).thenReturn(false);
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(optionInput));
        when(productRepository.findById(optionInput.getProduct().getId())).thenReturn(Optional.of(product));
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(optionSelect); // 새 옵션 저장

        // When
        ProductOptionResponse response = productOptionService.updateProductOption(optionId, changedTypeRequest);

        // Then
        assertThat(response.getOptionType()).isEqualTo(OptionType.SELECT);
        assertThat(response.getOptionType()).isEqualTo(changedTypeRequest.getOptionType());
        assertThat(optionInput.isActive()).isFalse(); // 기존 옵션 비활성화 확인
        verify(productOptionRepository, times(1)).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("옵션 수정 실패 - 주문된 옵션")
    void updateProductOptionFail_HasOptionOrders() {
        // Given
        when(orderService.hasOptionOrders(optionId)).thenReturn(true);
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(optionInput));

        // When & Then
        assertThatThrownBy(() -> productOptionService.updateProductOption(optionId, validUpdateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_CANNOT_BE_UPDATED.getMessage());
    }

    @Test
    @DisplayName("옵션 수정 실패 - 옵션이 존재하지 않음")
    void updateProductOptionFail_OptionNotFound() {
        // Given
        when(productOptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productOptionService.updateProductOption(invalidId, validUpdateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("옵션 삭제 성공")
    void deleteProductOptionSuccess() {
        // Given
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(optionInput));
        when(orderService.hasOptionOrders(optionId)).thenReturn(false);

        // When
        productOptionService.deleteProductOption(optionId);

        // Then
        verify(productOptionRepository, times(1)).delete(optionInput);
    }

    @Test
    @DisplayName("옵션 삭제 실패 - 주문된 옵션")
    void deleteProductOptionFail_HasOptionOrders() {
        // Given
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(optionInput));
        when(orderService.hasOptionOrders(optionId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> productOptionService.deleteProductOption(optionId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_CANNOT_BE_DELETED.getMessage());
    }

    @Test
    @DisplayName("옵션 삭제 실패 - 옵션이 존재하지 않음")
    void deleteProductOptionFail_OptionNotFound() {
        // Given
        when(productOptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productOptionService.deleteProductOption(invalidId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("옵션 활성화 성공")
    void activateProductOptionSuccess() {
        // Given
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(inactiveOption));
        when(productOptionRepository.countByProductIdAndIsActive(inactiveOption.getProduct().getId(), true)).thenReturn(2L);

        // When
        productOptionService.activateOption(optionId);

        // Then
        assertThat(inactiveOption.isActive()).isTrue();
    }

    @Test
    @DisplayName("옵션 활성화 실패 - 활성화된 옵션 3개 초과")
    void activateProductOptionFail_TooManyActiveOptions() {
        // Given
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(inactiveOption));
        when(productOptionRepository.countByProductIdAndIsActive(inactiveOption.getProduct().getId(), true)).thenReturn(3L);

        // When & Then
        assertThatThrownBy(() -> productOptionService.activateOption(optionId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("옵션 활성화 실패 - 옵션이 존재하지 않음")
    void activateProductOptionFail_OptionNotFound() {
        // Given
        when(productOptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productOptionService.activateOption(invalidId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("옵션 비활성화 성공")
    void deactivateProductOptionSuccess() {
        // Given
        when(productOptionRepository.findById(optionId)).thenReturn(Optional.of(optionInput));

        // When
        productOptionService.deactivateOption(optionId);

        // Then
        assertThat(optionInput.isActive()).isFalse();
    }

    @Test
    @DisplayName("옵션 비활성화 실패 - 옵션이 존재하지 않음")
    void deactivateProductOptionFail_OptionNotFound() {
        // Given
        when(productOptionRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productOptionService.deactivateOption(invalidId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.OPTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 상품의 모든 옵션 조회 성공 - 옵션이 있는 경우")
    void getAllOptionsByProductSuccess() {
        // Given
        when(productOptionRepository.findByProductId(productId)).thenReturn(List.of(optionInput, optionSelect, inactiveOption));

        // When
        List<ProductOptionResponse> options = productOptionService.getAllOptionsByProduct(productId);

        // Then
        assertThat(options).hasSize(3);
    }

    @Test
    @DisplayName("특정 상품의 모든 옵션 조회 성공 - 옵션이 없는 경우")
    void getAllOptionsByProductSuccess_Empty() {
        // Given
        when(productOptionRepository.findByProductId(productId)).thenReturn(List.of());

        // When
        List<ProductOptionResponse> options = productOptionService.getAllOptionsByProduct(productId);

        // Then
        assertThat(options).isEmpty();
    }

    @Test
    @DisplayName("특정 상품의 활성화된 옵션 조회 성공 - 활성화된 옵션이 있는 경우")
    void getActiveOptionsByProductSuccess() {
        // Given
        when(productOptionRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(List.of(optionInput, optionSelect));

        // When
        List<ProductOptionResponse> activeOptions = productOptionService.getActiveOptionsByProduct(productId);

        // Then
        assertThat(activeOptions).hasSize(2);
    }

    @Test
    @DisplayName("특정 상품의 활성화된 옵션 조회 성공 - 활성화된 옵션이 없는 경우")
    void getActiveOptionsByProductSuccess_Empty() {
        // Given
        when(productOptionRepository.findByProductIdAndIsActiveTrue(productId)).thenReturn(List.of());

        // When
        List<ProductOptionResponse> activeOptions = productOptionService.getActiveOptionsByProduct(productId);

        // Then
        assertThat(activeOptions).isEmpty();
    }

}