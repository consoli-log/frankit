package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.TestSecurityConfig;
import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.entity.Product;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.service.OrderService;
import com.soli.frankit.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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

    @MockitoBean
    private OrderService orderService;

    private Long validId;
    private Long invalidId;

    private ProductRequest validRequest;
    private ProductRequest blankNameRequest;
    private ProductRequest negativePriceRequest;

    private ProductRequest validUpdateRequest;
    private ProductRequest blankDescriptionRequest;
    private ProductRequest negativeFeeUpdateRequest;

    private ProductResponse validResponse;
    private ProductResponse validUpdateResponse;

    private Product activeProduct;
    private Product inactiveProduct;


    @BeforeEach
    void setUp() {
        validId = 1L;
        invalidId = 999L;

        validRequest = new ProductRequest("상품명", "상품 설명", BigDecimal.valueOf(20000), BigDecimal.valueOf(3000));
        blankNameRequest = new ProductRequest("", "상품 설명", BigDecimal.valueOf(20000), BigDecimal.valueOf(3000));
        negativePriceRequest = new ProductRequest("상품명", "상품 설명", BigDecimal.valueOf(-20000), BigDecimal.valueOf(3000));

        validUpdateRequest = new ProductRequest("상품명 수정", "상품 설명 수정", BigDecimal.valueOf(50000), BigDecimal.valueOf(1500));
        blankDescriptionRequest = new ProductRequest("상품명 수정", "", BigDecimal.valueOf(50000), BigDecimal.valueOf(1500));
        negativeFeeUpdateRequest = new ProductRequest("상품명 수정", "상품 설명 수정", BigDecimal.valueOf(50000), BigDecimal.valueOf(-1500));

        validResponse = ProductResponse.builder()
                                            .id(validId)
                                            .name("상품명")
                                            .description("상품 설명")
                                            .price(BigDecimal.valueOf(20000))
                                            .shippingFee(BigDecimal.valueOf(3000))
                                            .build();

        validUpdateResponse = ProductResponse.builder()
                                                .id(validId)
                                                .name("상품명 수정")
                                                .description("상품 설명 수정")
                                                .price(BigDecimal.valueOf(50000))
                                                .shippingFee(BigDecimal.valueOf(1500))
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

    }

    @Test
    @DisplayName("상품 등록 성공 (200)")
    void createProductSuccess() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(validResponse);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상품명"))
                .andExpect(jsonPath("$.description").value("상품 설명"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(20000)))
                .andExpect(jsonPath("$.shippingFee").value(BigDecimal.valueOf(3000)));
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
        mockMvc.perform(put("/api/products/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상품명 수정"))
                .andExpect(jsonPath("$.description").value("상품 설명 수정"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(50000)))
                .andExpect(jsonPath("$.shippingFee").value(BigDecimal.valueOf(1500)));
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품이 존재하지 않음 (404)")
    void updateProductFail_ProductNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).updateProduct(eq(invalidId), any(ProductRequest.class));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품 설명 공백 (400)")
    void updateProductFail_BlankDescription() throws Exception {
        mockMvc.perform(put("/api/products/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankDescriptionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("상품 설명은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("상품 수정 실패 - 배송비가 음수 (400)")
    void updateProductFail_NegativeFee() throws Exception {
        mockMvc.perform(put("/api/products/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(negativeFeeUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.shippingFee").value("배송비는 0원 이상이어야 합니다."));
    }

    @Test
    @DisplayName("상품 삭제 성공 - 주문되지 않은 상품(활성화 상태) (204)")
    void deleteProductSuccess_ActiveProductWithoutOrder() throws Exception {
        // Given
        when(orderService.hasOrders(validId)).thenReturn(false); // 주문되지 않음
        when(productService.getProductById(validId)).thenReturn(ProductResponse.from(activeProduct));

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", validId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(validId);
    }

    @Test
    @DisplayName("상품 삭제 성공 - 주문되지 않은 상품(비활성화 상태) (204)")
    void deleteProductSuccess_InactiveProductWithoutOrder() throws Exception {
        // Given
        when(orderService.hasOrders(validId)).thenReturn(false); // 주문되지 않음
        when(productService.getProductById(validId)).thenReturn(ProductResponse.from(inactiveProduct));

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", validId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(validId);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품이 존재하지 않음 (404)")
    void deleteProductFail_ProductNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).deleteProduct(invalidId);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상품 삭제 실패 - 주문된 상품 (400)")
    void deleteProductFail_ActiveProductWithOrder() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.PRODUCT_CANNOT_BE_DELETED))
                .when(productService).deleteProduct(validId);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", validId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_CANNOT_BE_DELETED.getMessage()));
    }

    @Test
    @DisplayName("상품 활성화 성공 (204)")
    void activateProductSuccess() throws Exception {
        mockMvc.perform(put("/api/products/{id}/activate", validId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).activateProduct(validId);
    }

    @Test
    @DisplayName("상품 활성화 실패 - 존재하지 않는 상품 (404)")
    void activateProductFail_ProductNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).activateProduct(invalidId);

        // When & Then
        mockMvc.perform(put("/api/products/{id}/activate", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상품 비활성화 성공 (204)")
    void deactivateProductSuccess() throws Exception {
        mockMvc.perform(put("/api/products/{id}/deactivate", validId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deactivateProduct(validId);
    }

    @Test
    @DisplayName("상품 비활성화 실패 - 존재하지 않는 상품 (404)")
    void deactivateProductFail_ProductNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .when(productService).deactivateProduct(invalidId);

        // When & Then
        mockMvc.perform(put("/api/products/{id}/deactivate", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }


    @Test
    @DisplayName("상품 조회 성공 (200)")
    void getProductByIdSuccess() throws Exception {
        // Given
        when(productService.getProductById(validId)).thenReturn(validResponse);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("상품명"))
                .andExpect(jsonPath("$.description").value("상품 설명"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(20000)))
                .andExpect(jsonPath("$.shippingFee").value(BigDecimal.valueOf(3000)));
    }

    @Test
    @DisplayName("상품 조회 실패 - 상품이 존재하지 않음 (404)")
    void getProductByIdFail_ProductNotFound() throws Exception {
        // Given
        when(productService.getProductById(invalidId))
                .thenThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 상품 있음 (200)")
    void getAllProductsSuccess() throws Exception {
        // Given
        int page = 0;
        int size = 2;

        List<ProductResponse> responseList = List.of(
                ProductResponse.builder().id(1L).name("상품 1").description("설명 1").price(BigDecimal.valueOf(10000)).shippingFee(BigDecimal.valueOf(1000)).build(),
                ProductResponse.builder().id(2L).name("상품 2").description("설명 2").price(BigDecimal.valueOf(20000)).shippingFee(BigDecimal.valueOf(2000)).build()
        );

        Page<ProductResponse> responsePage = new PageImpl<>(responseList, PageRequest.of(page, size), 3); // 한 페이지에 보여줄 상품은 2개지만 총 상품이 3개라고 설정
        when(productService.getAllProducts(page, size)).thenReturn(responsePage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2))) // 현재 페이지 데이터 2개인지 확인
                .andExpect(jsonPath("$.totalElements").value(3)) // 전체 데이터 3개인지 확인
                .andExpect(jsonPath("$.content[0].name").value("상품 1")); // 첫 번째 상품 이름 확인
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 상품 없음 (200)")
    void getAllProductsSuccess_Empty() throws Exception {
        // Given
        int page = 1;
        int size = 2;

        Page<ProductResponse> emptyPage = new PageImpl<>(List.of());
        when(productService.getAllProducts(page, size)).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(0))) // 현재 페이지 데이터 0개인지 확인
                .andExpect(jsonPath("$.totalElements").value(0)); // 전체 데이터 0개인지 확인
    }

}