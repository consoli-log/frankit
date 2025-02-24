package com.soli.frankit.controller;

import com.soli.frankit.dto.OptionDetailRequest;
import com.soli.frankit.dto.OptionDetailResponse;
import com.soli.frankit.service.OptionDetailService;
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
 * fileName     : OptionDetailController
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : 상세 옵션 관리를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/option-details")
@RequiredArgsConstructor
@Tag(name = "상품 상세 옵션 API", description = "상품 상세 옵션 CRUD 및 관리 API")
public class OptionDetailController {

    private final OptionDetailService optionDetailService;

    /**
     * 상세 옵션 등록 API
     *
     * @param optionId 옵션 ID
     * @param request 상세 옵션 등록 요청 DTO
     * @return 등록된 상세 옵션 정보
     */
    @PostMapping("/options/{optionId}")
    @Operation(summary = "상세 옵션 등록", description = "상품 옵션에 상세 옵션을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 옵션 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<OptionDetailResponse> createOptionDetail(
            @Parameter(description = "상세 옵션을 추가할 옵션 ID", example = "1") @PathVariable Long optionId,
            @Valid @RequestBody OptionDetailRequest request) {
        OptionDetailResponse response = optionDetailService.createOptionDetail(optionId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상세 옵션 수정 API
     *
     * @param detailId 수정할 상세 옵션 ID
     * @param request 상세 옵션 수정 요청 DTO
     * @return 수정된 상세 옵션 정보
     */
    @PutMapping("/{detailId}")
    @Operation(summary = "상세 옵션 수정", description = "등록된 상세 옵션을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 옵션 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "404", description = "상세 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "스정할 수 없는 상세 옵션"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<OptionDetailResponse> updateOptionDetail(
            @Parameter(description = "수정할 상세 옵션 ID", example = "1") @PathVariable Long detailId,
            @Valid @RequestBody OptionDetailRequest request) {
        OptionDetailResponse response = optionDetailService.updateOptionDetail(detailId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상세 옵션 삭제 API
     *
     * @param detailId 삭제할 상세 옵션 ID
     * @return 응답 코드 204 (No Content)
     */
    @DeleteMapping("/{detailId}")
    @Operation(summary = "상세 옵션 삭제", description = "등록된 상세 옵션을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상세 옵션 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상세 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제할 수 없는 상세 옵션"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> deleteOptionDetail(
            @Parameter(description = "삭제할 상세 옵션 ID", example = "1") @PathVariable Long detailId) {
        optionDetailService.deleteOptionDetail(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상세 옵션 활성화 API
     *
     * @param detailId 활성화할 상세 옵션 ID
     * @return 응답 코드 204 (No Content)
     */
    @PutMapping("/{detailId}/activate")
    @Operation(summary = "상세 옵션 활성화", description = "비활성화된 상세 옵션을 다시 활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상세 옵션 활성화 성공"),
            @ApiResponse(responseCode = "404", description = "상세 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 활성화된 상세 옵션"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> activateOptionDetail(
            @Parameter(description = "활성화할 상세 옵션 ID", example = "1") @PathVariable Long detailId) {
        optionDetailService.activateDetail(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상세 옵션 비활성화 API
     *
     * @param detailId 비활성화할 상세 옵션 ID
     * @return 응답 코드 204 (No Content)
     */
    @PutMapping("/{detailId}/deactivate")
    @Operation(summary = "상세 옵션 비활성화", description = "상세 옵션을 비활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상세 옵션 비활성화 성공"),
            @ApiResponse(responseCode = "404", description = "상세 옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> deactivateOptionDetail(
            @Parameter(description = "비활성화할 상세 옵션 ID", example = "1") @PathVariable Long detailId) {
        optionDetailService.deactivateDetail(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 옵션의 모든 상세 옵션 조회 API (활성화 + 비활성화 포함)
     *
     * @param optionId 옵션 ID
     * @return 해당 옵션의 모든 상세 옵션 리스트
     */
    @GetMapping("/options/{optionId}")
    @Operation(summary = "옵션별 상세 옵션 전체 조회", description = "특정 옵션의 모든 상세 옵션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 옵션 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<OptionDetailResponse>> getAllDetailsByOption(
            @Parameter(description = "조회할 옵션 ID", example = "1") @PathVariable Long optionId) {
        List<OptionDetailResponse> response = optionDetailService.getAllDetailsByOption(optionId);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 옵션의 활성화된 상세 옵션 조회 API
     *
     * @param optionId 옵션 ID
     * @return 해당 옵션의 활성화된 상세 옵션 리스트
     */
    @GetMapping("/options/{optionId}/active")
    @Operation(summary = "옵션별 활성화된 상세 옵션 조회", description = "특정 옵션의 활성화된 상세 옵션을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 옵션 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "옵션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<OptionDetailResponse>> getActiveDetailsByOption(
            @Parameter(description = "조회할 옵션 ID", example = "1") @PathVariable Long optionId) {
        List<OptionDetailResponse> response = optionDetailService.getActiveDetailsByOption(optionId);
        return ResponseEntity.ok(response);
    }

}
