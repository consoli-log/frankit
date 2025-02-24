package com.soli.frankit.controller;

import com.soli.frankit.dto.OptionDetailRequest;
import com.soli.frankit.dto.OptionDetailResponse;
import com.soli.frankit.service.OptionDetailService;
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
    public ResponseEntity<OptionDetailResponse> createOptionDetail(@PathVariable Long optionId,
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
    public ResponseEntity<OptionDetailResponse> updateOptionDetail(@PathVariable Long detailId,
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
    public ResponseEntity<Void> deleteOptionDetail(@PathVariable Long detailId) {
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
    public ResponseEntity<Void> activateOptionDetail(@PathVariable Long detailId) {
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
    public ResponseEntity<Void> deactivateOptionDetail(@PathVariable Long detailId) {
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
    public ResponseEntity<List<OptionDetailResponse>> getAllDetailsByOption(@PathVariable Long optionId) {
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
    public ResponseEntity<List<OptionDetailResponse>> getActiveDetailsByOption(@PathVariable Long optionId) {
        List<OptionDetailResponse> response = optionDetailService.getActiveDetailsByOption(optionId);
        return ResponseEntity.ok(response);
    }

}
