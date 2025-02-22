package com.soli.frankit.service;

import com.soli.frankit.config.TestEnvConfig;
import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.entity.Product;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * packageName  : com.soli.frankit.service
 * fileName     : ProductServiceTest
 * author       : eumsoli
 * date         : 2025-02-21
 * description  : ProductService의 상품 관리 기능 테스트
 */
@ExtendWith(MockitoExtension.class)
@Import(TestEnvConfig.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Long vaildId;
    private Long invaildId;

    private Product validProduct;

    private ProductRequest validCreateRequest;
    private ProductRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        vaildId = 1L;
        invaildId = 999L;

        validProduct = Product.builder()
                                .name("상품명")
                                .description("상품 설명")
                                .price(BigDecimal.valueOf(20000))
                                .build();

        validCreateRequest = new ProductRequest("새 상품명", "새 상품 설명", BigDecimal.valueOf(10000), BigDecimal.valueOf(4000));
        validUpdateRequest = new ProductRequest("상품명 수정", "상품 설명 수정", BigDecimal.valueOf(50000), BigDecimal.valueOf(1500));
    }

    @Test
    @DisplayName("상품 등록 성공")
    void createProductSuccess() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(validProduct);

        // When
        ProductResponse response = productService.createProduct(validCreateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(validProduct.getName());
        assertThat(response.getDescription()).isEqualTo(validProduct.getDescription());
        assertThat(response.getPrice()).isEqualTo(validProduct.getPrice());
        assertThat(response.getShippingFee()).isEqualTo(validProduct.getShippingFee());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProductSuccess() {
        // Given
        when(productRepository.findById(vaildId)).thenReturn(Optional.of(validProduct));

        // When
        ProductResponse response = productService.updateProduct(vaildId, validUpdateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(validProduct.getName());
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품이 존재하지 않음")
    void updateProductFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invaildId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(invaildId, validUpdateRequest)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProductSuccess() {
        // Given
        when(productRepository.findById(vaildId)).thenReturn(Optional.of(validProduct));

        // When
        productService.deleteProduct(vaildId);

        // Then
        verify(productRepository, times(1)).delete(validProduct);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품이 존재하지 않음")
    void deleteProductFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invaildId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(invaildId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

}