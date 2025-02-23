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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
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

    @Mock
    private OrderService orderService;

    private Long validId;
    private Long invalidId;

    private Product validProduct;
    private Product activeProduct;
    private Product inactiveProduct;

    private ProductRequest validCreateRequest;
    private ProductRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        validId = 1L;
        invalidId = 999L;

        validProduct = Product.builder()
                                .name("상품명")
                                .description("상품 설명")
                                .price(BigDecimal.valueOf(20000))
                                .shippingFee(BigDecimal.valueOf(3000))
                                .build();

        activeProduct = Product.builder()
                                .name("활성화 상품명")
                                .description("활성화 상품 설명")
                                .price(BigDecimal.valueOf(10000))
                                .shippingFee(BigDecimal.valueOf(1000))
                                .build();

        inactiveProduct = Product.builder()
                                    .name("비활성화 상품명")
                                    .description("비활성화 상품 설명")
                                    .price(BigDecimal.valueOf(15000))
                                    .shippingFee(BigDecimal.valueOf(1500))
                                    .build();
        inactiveProduct.deactivate();

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
        when(productRepository.findById(validId)).thenReturn(Optional.of(validProduct));

        // When
        ProductResponse response = productService.updateProduct(validId, validUpdateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(validProduct.getName());
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품이 존재하지 않음")
    void updateProductFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(invalidId, validUpdateRequest)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품 비활성화 성공")
    void deactivateProductSuccess() {
        // Given
        when(productRepository.findById(validId)).thenReturn(Optional.of(activeProduct));

        // When
        productService.deactivateProduct(validId);

        // Then
        assertThat(activeProduct.isActive()).isFalse();
        verify(productRepository, times(1)).findById(validId);
    }

    @Test
    @DisplayName("상품 비활성화 실패 - 존재하지 않는 상품")
    void deactivateProductFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deactivateProduct(invalidId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품 삭제 성공 - 주문되지 않은 상품(활성화 상태)")
    void deleteProductSuccess_ActiveProductWithoutOrder() {
        // Given
        when(orderService.hasOrders(validId)).thenReturn(false); // 주문되지 않은 상태
        when(productRepository.findById(validId)).thenReturn(Optional.of(activeProduct));

        // When
        productService.deleteProduct(validId);

        // Then
        verify(productRepository, times(1)).delete(activeProduct);
    }

    @Test
    @DisplayName("상품 삭제 성공 - 주문되지 않은 상품(활성화 상태)")
    void deleteProductSuccess_InactiveProductWithoutOrder() {
        // Given
        when(orderService.hasOrders(validId)).thenReturn(false); // 주문되지 않은 상태
        when(productRepository.findById(validId)).thenReturn(Optional.of(inactiveProduct));

        // When
        productService.deleteProduct(validId);

        // Then
        verify(productRepository, times(1)).delete(inactiveProduct);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품이 존재하지 않음")
    void deleteProductFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(invalidId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품 삭제 실패 - 주문된 상품(활성화 상태)")
    void deleteProductFail_ActiveProductWithOrder() {
        // Given
        when(orderService.hasOrders(validId)).thenReturn(true); // 주문됨
        when(productRepository.findById(validId)).thenReturn(Optional.of(activeProduct));

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(validId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_CANNOT_BE_DELETED.getMessage());
    }

    @Test
    @DisplayName("상품 삭제 실패 - 주문된 상품(비활성화 상태)")
    void deleteProductFail_InactiveProductWithOrder() {
        // Given
        when(orderService.hasOrders(validId)).thenReturn(true); // 주문됨
        when(productRepository.findById(validId)).thenReturn(Optional.of(inactiveProduct));

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(validId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_CANNOT_BE_DELETED.getMessage());
    }

    @Test
    @DisplayName("상품 조회 성공")
    void getProductByIdSuccess() {
        // Given
        when(productRepository.findById(validId)).thenReturn(Optional.of(validProduct));

        // When
        ProductResponse response = productService.getProductById(validId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(validProduct.getName());
        assertThat(response.getDescription()).isEqualTo(validProduct.getDescription());
        assertThat(response.getPrice()).isEqualTo(validProduct.getPrice());
        assertThat(response.getShippingFee()).isEqualTo(validProduct.getShippingFee());
    }

    @Test
    @DisplayName("상품 조회 실패 - 상품이 존재하지 않음")
    void getProductByIdFail_ProductNotFound() {
        // Given
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(invalidId)).isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 상품 있음")
    void getAllProductsSuccess() {
        // Given
        int page = 0;
        int size = 2;

        List<Product> productList = List.of(
                Product.builder().name("상품 1").description("설명 1").price(BigDecimal.valueOf(10000)).shippingFee(BigDecimal.valueOf(1000)).build(),
                Product.builder().name("상품 2").description("설명 2").price(BigDecimal.valueOf(20000)).shippingFee(BigDecimal.valueOf(2000)).build()
        );
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(page, size), 3); // 한 페이지에 보여줄 상품은 2개지만 총 상품이 3개라고 설정
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // When
        Page<ProductResponse> response = productService.getAllProducts(page, size);

        // Then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getContent().get(0).getName()).isEqualTo("상품 1");
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 상품 없음")
    void getAllProductsSuccess_Empty() {
        // Given
        int page = 1;
        int size = 2;

        Page<Product> emptyPage = new PageImpl<>(List.of());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<ProductResponse> response = productService.getAllProducts(page, size);

        // Then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
    }

}