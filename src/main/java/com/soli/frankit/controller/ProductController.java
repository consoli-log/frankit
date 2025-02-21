package com.soli.frankit.controller;

import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * packageName  : com.soli.frankit.controller.product
 * fileName     : ProductController
 * author       : eumsoli
 * date         : 2025-02-21
 * description  : 상품 관리를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 API
     *
     * @param  request 상품 등록 요청 DTO
     * @return 등록된 상품 정보
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 수정 API
     *
     * @param id 수정할 상품 ID
     * @param request 수정할 상품 정보 DTO
     * @return 수정된 상품 정보 응답 DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 삭제 API
     *
     * @param id 삭제할 상품 ID
     * @return 응답 코드 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
