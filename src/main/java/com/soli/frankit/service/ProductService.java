package com.soli.frankit.service;

import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.entity.Product;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * packageName  : com.soli.frankit.service
 * fileName     : ProductService
 * author       : eumsoli
 * date         : 2025-02-21
 * description  : 상품 관리를 담당하는 서비스 클래스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 등록
     *
     * @param request 상품 등록 요청 DTO
     * @return 등록된 상품 정보
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                                    .name(request.getName())
                                    .description(request.getDescription())
                                    .price(request.getPrice())
                                    .build();

        Product savedProduct = productRepository.save(product);
        log.info("상품 등록 완료: id={}, name={}, description={}, price={}"
                , savedProduct.getId(), savedProduct.getName(), savedProduct.getDescription(), savedProduct.getPrice() );

        return convertToResponseDto(savedProduct);
    }

    /**
     * 상품 수정
     *
     * @param id 수정할 상품 ID
     * @param request 수정할 상품 정보
     * @return 수정된 상품 정보
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(request.getName(), request.getDescription(), request.getPrice());
        log.info("상품 수정 완료: id={}, name={}, description={}, price={}"
                , product.getId(), product.getName(), product.getDescription(), product.getPrice() );

        return convertToResponseDto(product);
    }

    /**
     * 상품 삭제
     *
     * @param  id 식제할 상품 ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
        log.info("상품 삭제 완료: id={}", id);
    }

    /**
     * 상품 정보를 DTO로 변환
     *
     * @param product 상품 엔티티
     * @return 상품 응답 DTO
     */
    private ProductResponse convertToResponseDto(Product product) {
        return ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())
                                .build();
    }

}
