package com.soli.frankit.service;

import com.soli.frankit.dto.OptionDetailRequest;
import com.soli.frankit.dto.OptionDetailResponse;
import com.soli.frankit.entity.OptionDetail;
import com.soli.frankit.entity.OptionType;
import com.soli.frankit.entity.ProductOption;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.OptionDetailRepository;
import com.soli.frankit.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName  : com.soli.frankit.service
 * fileName     : OptionDetailService
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : 상품 상세 옵션 관리를 담당하는 서비스 클래스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionDetailService {

    private final ProductOptionRepository productOptionRepository;
    private final OptionDetailRepository optionDetailRepository;
    private final OrderService orderService;

    /**
     * 상세 옵션 등록
     *
     * @param optionId 옵션 ID
     * @param request 상세 옵션 등록 요청 DTO
     * @return 등록된 상세 옵션 정보
     */
    @Transactional
    public OptionDetailResponse createOptionDetail(Long optionId, OptionDetailRequest request) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        // 입력형 옵션에는 상세 옵션을 추가할 수 없음
        if (option.getOptionType() == OptionType.INPUT) {
            throw new CustomException(ErrorCode.OPTION_CANNOT_HAVE_DETAILS);
        }

        OptionDetail optionDetail = OptionDetail.builder()
                                                .productOption(option)
                                                .detailName(request.getDetailName())
                                                .detailPrice(request.getDetailPrice())
                                                .build();

        OptionDetail savedDetail = optionDetailRepository.save(optionDetail);
        log.info("상세 옵션 등록 완료: id={}, detailName={}, detailPrice={}",
                savedDetail.getId(), savedDetail.getDetailName(), savedDetail.getDetailPrice());

        return convertToResponseDto(savedDetail);
    }

    /**
     * 상세 옵션 수정
     *
     * @param detailId 수정할 상세 옵션 ID
     * @param request 수정할 상세 옵션 정보
     * @return 수정된 상세 옵션 정보
     */
    @Transactional
    public OptionDetailResponse updateOptionDetail(Long detailId, OptionDetailRequest request) {
        OptionDetail detail = optionDetailRepository.findById(detailId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND));

        boolean hasDetailOrder = orderService.hasDetailOrders(detailId);
        log.info("상세 옵션 수정 요청 - detailId: {}, hasDetailOrder: {}", detailId, hasDetailOrder);

        // 기존 상세 옵션이 주문된 상태라면 수정 불가
        if (!detail.isUpdatable(hasDetailOrder)) {
            log.warn("상세 옵션 수정 불가 - 주문된 상세 옵션은 수정할 수 없습니다.");
            throw new CustomException(ErrorCode.OPTION_DETAIL_CANNOT_BE_UPDATED);
        }

        detail.update(request.getDetailName(), request.getDetailPrice(), hasDetailOrder);
        log.info("상세 옵션 수정 완료: detailId={}, detailName={}, detailPrice={}",
                detail.getId(), detail.getDetailName(), detail.getDetailPrice());

        return convertToResponseDto(detail);
    }

    /**
     * 상세 옵션 삭제
     *
     * @param detailId 삭제할 상세 옵션 ID
     */
    @Transactional
    public void deleteOptionDetail(Long detailId) {
        OptionDetail detail = optionDetailRepository.findById(detailId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND));

        boolean hasDetailOrder = orderService.hasDetailOrders(detailId);
        log.info("상세 옵션 삭제 요청 - detailId: {}, hasDetailOrder: {}", detailId, hasDetailOrder);

        if (!detail.isDeletable(hasDetailOrder)) {
            log.warn("상세 옵션 삭제 불가 - 주문된 상세 옵션은 삭제할 수 없습니다.");
            throw new CustomException(ErrorCode.OPTION_DETAIL_CANNOT_BE_DELETED);
        }

        optionDetailRepository.delete(detail);
        log.info("상세 옵션 삭제 완료: detailId={}", detail.getId());
    }

    /**
     * 상세 옵션 활성화
     *
     * @param detailId 활성화할 상세 옵션 ID
     */
    @Transactional
    public void activateDetail(Long detailId) {
        OptionDetail detail = optionDetailRepository.findById(detailId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND));

        detail.activate();
        log.info("상세 옵션 활성화 완료: detailId={}", detail.getId());
    }

    /**
     * 상세 옵션 비활성화
     *
     * @param detailId 비활성화할 상세 옵션 ID
     */
    @Transactional
    public void deactivateDetail(Long detailId) {
        OptionDetail detail = optionDetailRepository.findById(detailId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_DETAIL_NOT_FOUND));

        detail.deactivate();
        log.info("상세 옵션 비활성화 완료: detailId={}", detail.getId());
    }

    /**
     * 특정 옵션의 모든 상세 옵션 조회 (활성화 + 비활성화)
     *
     * @param optionId 옵션 ID
     * @return 해당 옵션의 모든 상세 옵션 리스트
     */
    @Transactional(readOnly = true)
    public List<OptionDetailResponse> getAllDetailsByOption(Long optionId) {
        ProductOption productOption = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        List<OptionDetail> details = optionDetailRepository.findByProductOption(productOption);
        log.info("옵션의 모든 상세 옵션 조회 - optionId: {}, detailsCount: {}", optionId, details.size());

        return details.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

    /**
     * 특정 옵션의 활성화된 상세 옵션 조회
     *
     * @param optionId 옵션 ID
     * @return 해당 옵션의 활성화된 상세 옵션 리스트
     */
    @Transactional(readOnly = true)
    public List<OptionDetailResponse> getActiveDetailsByOption(Long optionId) {
        ProductOption productOption = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        List<OptionDetail> activeDetails = optionDetailRepository.findByProductOptionAndIsActiveTrue(productOption);

        if (activeDetails.isEmpty()) {
            log.warn("옵션의 활성화된 상세 옵션이 없습니다 - optionId: {}", optionId);
        } else {
            log.info("옵션의 활성화된 상세 옵션 조회 - optionId: {}, activeDetailsCount: {}", optionId, activeDetails.size());
        }

        return activeDetails.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

    /**
     * 상세 옵션 정보를 DTO로 변환
     *
     * @param detail 상세 옵션 엔티티
     * @return 상세 옵션 응답 DTO
     */
    private OptionDetailResponse convertToResponseDto(OptionDetail detail) {
        return OptionDetailResponse.from(detail);
    }

}
