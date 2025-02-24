package com.soli.frankit.controller;

import com.soli.frankit.dto.ProductRequest;
import com.soli.frankit.dto.ProductResponse;
import com.soli.frankit.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@Tag(name = "상품 API", description = "상품 CRUD 및 관리 API")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 API
     *
     * @param request 상품 등록 요청 DTO
     * @return 등록된 상품 정보
     */
    @PostMapping
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 수정 API
     *
     * @param productId 수정할 상품 ID
     * @param request 수정할 상품 정보 DTO
     * @return 수정된 상품 정보 응답 DTO
     */
    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "수정할 상품 ID", example = "1") @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 삭제 API
     *
     * @param productId 삭제할 상품 ID
     * @return 응답 코드 204 (No Content)
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제할 수 없는 상품"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "삭제할 상품 ID", example = "1") @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 활성화 API
     *
     * @param productId 활성화할 상품 ID
     * @return 응답 코드 204 (No Content)
     */
    @PutMapping("/{productId}/activate")
    @Operation(summary = "상품 활성화", description = "비활성화된 상품을 다시 활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 활성화 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 활성화된 상품"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> activateProduct(
            @Parameter(description = "활성화할 상품 ID", example = "1") @PathVariable Long productId) {
        productService.activateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 비활성화 API
     *
     * @param productId 비활성화할 상품 ID
     * @return 응답 코드 204 (No Content)
     */
    @PutMapping("/{productId}/deactivate")
    @Operation(summary = "상품 비활성화", description = "상품을 비활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 비활성화 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> deactivateProduct(
            @Parameter(description = "비활성화할 상품 ID", example = "1") @PathVariable Long productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 단건 조회 API
     *
     * @param productId 조회할 상품 ID
     * @return 조회된 상품 정보
     */
    @GetMapping("/{productId}")
    @Operation(summary = "상품 조회", description = "상품 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "조회할 상품 ID", example = "1") @PathVariable Long productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 목록 조회 API (페이징)
     *
     * @param page 조회할 페이지 번호 (기본값: 0)
     * @param size 페이지당 항목 수 (기본값: 10)
     * @return 페이징된 상품 목록 정보
     */
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "등록된 상품 목록을 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "조회할 페이지 번호 (기본값: 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수 (기본값: 10)", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

}
