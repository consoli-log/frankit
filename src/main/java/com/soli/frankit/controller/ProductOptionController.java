package com.soli.frankit.controller;

import com.soli.frankit.dto.ProductOptionRequest;
import com.soli.frankit.dto.ProductOptionResponse;
import com.soli.frankit.service.ProductOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "상품 옵션 API", description = "상품 옵션 CRUD 및 관리 API")
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
    @Operation(summary = "상품 옵션 등록", description = "상품에 새로운 옵션을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 옵션 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "옵션 개수 초과"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ProductOptionResponse> createProductOption(
            @Parameter(description = "옵션을 등록할 상품 ID", example = "1") @PathVariable Long productId,
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
    @Operation(summary = "상품 옵션 수정", description = "상품 옵션 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 옵션 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "상품 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "수정할 수 없는 옵션"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ProductOptionResponse> updateProductOption(
            @Parameter(description = "수정할 옵션 ID", example = "1") @PathVariable Long optionId,
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
    @Operation(summary = "상품 옵션 삭제", description = "상품 옵션을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상품 옵션 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상품 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "삭제할 수 없는 옵션"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> deleteProductOption(
            @Parameter(description = "삭제할 옵션 ID", example = "1") @PathVariable Long optionId) {
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
    @Operation(summary = "상품 옵션 활성화", description = "비활성화된 상품 옵션을 다시 활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 옵션 활성화 성공"),
            @ApiResponse(responseCode = "404", description = "상품 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 활성화된 옵션"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> activateProductOption(
            @Parameter(description = "활성화할 옵션 ID", example = "1") @PathVariable Long optionId) {
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
    @Operation(summary = "상품 옵션 비활성화", description = "상품 옵션을 비활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 옵션 비활성화 성공"),
            @ApiResponse(responseCode = "404", description = "상품 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> deactivateProductOption(
            @Parameter(description = "비활성화할 옵션 ID", example = "1") @PathVariable Long optionId) {
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
    @Operation(summary = "상품 옵션 조회", description = "상품에 등록된 모든 옵션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 옵션 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<ProductOptionResponse>> getAllOptionsByProduct(
            @Parameter(description = "옵션을 조회할 상품 ID", example = "1") @PathVariable Long productId) {
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
    @Operation(summary = "상품 활성화 옵션 조회", description = "상품에 등록된 활성화된 옵션만 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 활성화 옵션 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<ProductOptionResponse>> getActiveOptionsByProduct(
            @Parameter(description = "활성화된 옵션을 조회할 상품 ID", example = "1") @PathVariable Long productId) {
        List<ProductOptionResponse> response = productOptionService.getActiveOptionsByProduct(productId);
        return ResponseEntity.ok(response);
    }

}
