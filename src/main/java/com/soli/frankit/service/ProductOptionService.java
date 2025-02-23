package com.soli.frankit.service;

import com.soli.frankit.dto.ProductOptionRequest;
import com.soli.frankit.dto.ProductOptionResponse;
import com.soli.frankit.entity.Product;
import com.soli.frankit.entity.ProductOption;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.ProductOptionRepository;
import com.soli.frankit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName  : com.soli.frankit.service
 * fileName     : ProductOptionService
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 옵션 관리를 담당하는 서비스 클래스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final OrderService orderService;

    /**
     * 상품 옵션 등록
     *
     * @param id 상품 ID
     * @param request 옵션 등록 요청 DTO
     * @return 등록된 옵션 정보
     */
    @Transactional
    public ProductOptionResponse createProductOption(Long id, ProductOptionRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 현재 활성화된 옵션 개수 확인 (비활성화 옵션 제외)
        long activeOptionCount = productOptionRepository.countByProductIdAndIsActive(id, true);

        if (activeOptionCount >= 3) {
            throw new CustomException(ErrorCode.OPTION_LIMIT_EXCEEDED);
        }

        ProductOption option = ProductOption.builder()
                                                .product(product)
                                                .optionName(request.getOptionName())
                                                .optionType(request.getOptionType())
                                                .optionPrice(request.getOptionPrice())
                                                .build();

        ProductOption savedOption = productOptionRepository.save(option);
        log.info("옵션 등록 완료: id={}, optionName={}, optionType={}, optionPrice={}"
                , savedOption.getId(), savedOption.getOptionName(), savedOption.getOptionType(), savedOption.getOptionPrice());

        return convertToResponseDto(savedOption);
    }

    /**
     * 상품 옵션 수정
     *
     * @param id 수정할 상품 옵션 ID
     * @param request 수정할 상품 옵션 정보
     * @return 수정된 상품 옵션 정보
     */
    @Transactional
    public ProductOptionResponse updateProductOption(Long id, ProductOptionRequest request) {
        ProductOption option = productOptionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        boolean hasOptionOrder = orderService.hasOptionOrders(id);
        log.info("옵션 수정 요청 - ID: {}, hasOptionOrder: {}, isActive: {}", id, hasOptionOrder, option.isActive());

        // 기존 옵션이 주문된 상태라면 수정 불가
        if (!option.isUpdatable(hasOptionOrder)) {
            log.warn("옵션 수정 불가 - 주문된 상품은 수정할 수 없습니다.");
            throw new CustomException(ErrorCode.OPTION_CANNOT_BE_UPDATED);
        }

        // 옵션 타입이 변경된 경우 기존 옵션 비활성화 후 새 옵션 추가
        if (!option.getOptionType().equals(request.getOptionType())) {
            log.info("옵션 타입 변경 - 기존: {}, 변경: {}", option.getOptionType(), request.getOptionType());
            option.deactivate();

            return createProductOption(option.getProduct().getId(), request);
        }

        option.update(request.getOptionName(), request.getOptionType(), request.getOptionPrice(), hasOptionOrder);
        log.info("옵션 수정 완료: id={}, optionName={}, optionType={}, optionPrice={}"
                , option.getId(), option.getOptionName(), option.getOptionType(), option.getOptionPrice());

        return convertToResponseDto(option);
    }

    /**
     * 상품 옵션 삭제
     *
     * @param id 삭제할 상품 옵션 ID
     */
    @Transactional
    public void deleteProductOption(Long id) {
        ProductOption option = productOptionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        boolean hasOptionOrder = orderService.hasOptionOrders(id);
        log.info("옵션 삭제 요청 - ID: {}, hasOptionOrder: {}, isActive: {}", id, hasOptionOrder, option.isActive());

        // 기존 옵션이 주문된 상태라면 삭제 불가
        if (!option.isDeletable(hasOptionOrder)) {
            log.warn("옵션 삭제 불가 - 주문된 상품은 삭제할 수 없습니다.");
            throw new CustomException(ErrorCode.OPTION_CANNOT_BE_DELETED);
        }

        productOptionRepository.delete(option);
        log.info("옵션 삭제 완료: id={}", option.getId());
    }

    /**
     * 옵션 활성화
     *
     * @param id 활성화할 옵션 ID
     */
    @Transactional
    public void activateOption(Long id) {
        ProductOption option = productOptionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        long activeOptionCount = productOptionRepository.countByProductIdAndIsActive(option.getProduct().getId(), true);

        option.activate((int) activeOptionCount);
        log.info("옵션 활성화 완료: id={}", option.getId());
    }

    /**
     * 옵션 비활성화
     *
     * @param id 비활성화할 옵션 ID
     */
    @Transactional
    public void deactivateOption(Long id) {
        ProductOption option = productOptionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        option.deactivate();
        log.info("옵션 비활성화 완료: id={}", option.getId());
    }

    /**
     * 상품의 모든 옵션 조회 (활성화 + 비활성화)
     *
     * @param id 상품 ID
     * @return 해당 상품의 모든 옵션 리스트
     */
    @Transactional(readOnly = true)
    public List<ProductOptionResponse> getAllOptionsByProduct(Long id) {
        List<ProductOption> options = productOptionRepository.findByProductId(id);
        log.info("상품의 모든 옵션 조회 - id: {}, optionsCount: {}", id, options.size());

        return options.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

    /**
     * 상품의 활성화된 옵션 조회
     *
     * @param id 상품 ID
     * @return 해당 상품의 활성화된 옵션 리스트
     */
    @Transactional(readOnly = true)
    public List<ProductOptionResponse> getActiveOptionsByProduct(Long id) {
        List<ProductOption> activeOptions = productOptionRepository.findByProductIdAndIsActiveTrue(id);

        if (activeOptions.isEmpty()) {
            log.warn("상품의 활성화된 옵션이 없습니다 - id: {}", id);
        } else {
            log.info("상품의 활성화된 옵션 조회 - id: {}, activeOptionsCount: {}", id, activeOptions.size());
        }

        return activeOptions.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

    /**
     * 상품 옵션 정보를 DTO로 변환
     *
     * @param option 상품 옵션 엔티티
     * @return 상품 옵션 응답 DTO
     */
    private ProductOptionResponse convertToResponseDto(ProductOption option) {
        return ProductOptionResponse.from(option);
    }

}
