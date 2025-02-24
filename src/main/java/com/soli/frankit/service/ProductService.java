package com.soli.frankit.service;

import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.entity.Product;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final OrderService orderService;

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
                                    .shippingFee(request.getShippingFee())
                                    .build();

        Product savedProduct = productRepository.save(product);
        log.info("상품 등록 완료: {}", savedProduct);

        return convertToResponseDto(savedProduct);
    }

    /**
     * 상품 수정
     *
     * @param productId 수정할 상품 ID
     * @param request 수정할 상품 정보
     * @return 수정된 상품 정보
     */
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(request.getName(), request.getDescription(), request.getPrice(), request.getShippingFee());
        log.info("상품 수정 완료: {}", product);

        return convertToResponseDto(product);
    }

    /**
     * 상품 삭제
     *
     * @param  productId 삭제할 상품 ID
     */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        boolean hasOrder = orderService.hasOrders(productId); // 주문 여부 확인
        log.info("상품 삭제 요청 - productId: {}, hasOrder: {}, isActive: {}", productId, hasOrder, product.isActive());

        // 삭제 가능 여부 확인
        if (!product.isDeletable(hasOrder)) {
            log.warn("상품 삭제 불가 - 주문된 상품은 삭제할 수 없습니다.");
            throw new CustomException(ErrorCode.PRODUCT_CANNOT_BE_DELETED);
        }

        productRepository.delete(product);
        log.info("상품 삭제 완료: productId={}", productId);
    }

    /**
     * 상품 활성화
     *
     * @param productId 활성화할 상품 ID
     */
    @Transactional
    public void activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.activate();
        log.info("상품 활성화 완료: {}", product);
    }

    /**
     * 상품 비활성화
     *
     * @param productId 비활성화할 상품 ID
     */
    @Transactional
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.deactivate();
        log.info("상품 비활성화 완료: {}", product);
    }

    /**
     * 상품 단건 조회
     *
     * @param productId 조회할 상품 ID
     * @return 조회된 상품 정보
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        log.info("상품 조회 완료: {}", product);

        return convertToResponseDto(product);
    }

    /**
     * 상품 목록 조회 (페이징)
     *
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 페이징된 상품 목록 정보
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending()); // 생성일자 내림차순

        Page<Product> products = productRepository.findAll(pageable);

        log.info("상품 목록 조회 완료: page={}, size={}, totalElements={}", page, size, products.getTotalElements());

        products.getContent().forEach(product ->
                log.info("상품 목록 정보: id={}, name={}, price={}, shippingFee={}",
                        product.getId(), product.getName(), product.getPrice(), product.getShippingFee())
        );

        return products.map(this::convertToResponseDto);
    }

    /**
     * 상품 정보를 DTO로 변환
     *
     * @param product 상품 엔티티
     * @return 상품 응답 DTO
     */
    private ProductResponse convertToResponseDto(Product product) {
        return ProductResponse.from(product);
    }

}
