package com.soli.frankit.controller;

import com.soli.frankit.dto.ProductOptionRequest;
import com.soli.frankit.dto.ProductOptionResponse;
import com.soli.frankit.service.ProductOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName  : com.soli.frankit.controller
 * fileName     : ProductOptionController
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 옵션 관리를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/product-options")
@RequiredArgsConstructor
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    /**
     * 상품 옵션 등록 API
     *
     * @param productId 상품 ID
     * @param request 옵션 등록 요청 DTO
     * @return 등록된 옵션 정보
     */
    @PostMapping("/products/{productId}")
    public ResponseEntity<ProductOptionResponse> createProductOption(@PathVariable Long productId,
                                                                     @Valid @RequestBody ProductOptionRequest request) {
        ProductOptionResponse response = productOptionService.createProductOption(productId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 옵션 수정 API
     *
     * @param optionId 수정할 상품 옵션 ID
     * @param request 수정할 상품 옵션 정보 DTO
     * @return 수정된 상품 옵션 정보
     */
    @PutMapping("/{optionId}")
    public ResponseEntity<ProductOptionResponse> updateProductOption(@PathVariable Long optionId,
                                                                     @Valid @RequestBody ProductOptionRequest request) {
        ProductOptionResponse response = productOptionService.updateProductOption(optionId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 옵션 삭제 API
     *
     * @param optionId 삭제할 상품 옵션 ID
     * @return 응답 코드 204 (No Content)
     */
    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteProductOption(@PathVariable Long optionId) {
        productOptionService.deleteProductOption(optionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 옵션 활성화 API
     *
     * @param optionId 활성화할 상품 옵션 ID
     * @return 응답 코드 204 (No Content)
     */
    @PutMapping("/{optionId}/activate")
    public ResponseEntity<Void> activateProductOption(@PathVariable Long optionId) {
        productOptionService.activateOption(optionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 옵션 비활성화 API
     *
     * @param optionId 비활성화할 상품 옵션 ID
     * @return 응답 코드 204 (No Content)
     */
    @PutMapping("/{optionId}/deactivate")
    public ResponseEntity<Void> deactivateProductOption(@PathVariable Long optionId) {
        productOptionService.deactivateOption(optionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품의 모든 옵션 조회 API (활성화 + 비활성화 포함)
     *
     * @param productId 상품 ID
     * @return 해당 상품의 모든 옵션 리스트
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<List<ProductOptionResponse>> getAllOptionsByProduct(@PathVariable Long productId) {
        List<ProductOptionResponse> response = productOptionService.getAllOptionsByProduct(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품의 활성화된 옵션 조회 API
     *
     * @param productId 상품 ID
     * @return 해당 상품의 활성화된 옵션 리스트
     */
    @GetMapping("/products/{productId}/active")
    public ResponseEntity<List<ProductOptionResponse>> getActiveOptionsByProduct(@PathVariable Long productId) {
        List<ProductOptionResponse> response = productOptionService.getActiveOptionsByProduct(productId);
        return ResponseEntity.ok(response);
    }


}
