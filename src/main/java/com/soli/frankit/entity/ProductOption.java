package com.soli.frankit.entity;

import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName  : com.soli.frankit.entity
 * fileName     : ProductOption
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 옵션 정보를 저장하는 엔티티
 */

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_options")
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_seq")
    private Long id; // 옵션 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_seq", nullable = false)
    private Product product; // 연결된 상품

    @Column(nullable = false)
    private String optionName; // 옵션 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionType optionType; // 옵션 타입

    @Column(precision = 10, scale = 2)
    private BigDecimal optionPrice; // 옵션 추가 금액 (입력형)

    @OneToMany(mappedBy = "productOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionDetail> optionDetails = new ArrayList<>();  // 상세 옵션 (선택형)

    @Column(nullable = false)
    private boolean isActive = true; // 옵션 활성화 여부 (기본값 true)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    /**
     * ProductOption 생성자
     *
     * @param product 연결된 상품
     * @param optionName 옵션명
     * @param optionType 옵션 타입
     * @param optionPrice 옵션 추가 금액 (입력형)
     */
    @Builder
    public ProductOption(Product product, String optionName, OptionType optionType, BigDecimal optionPrice) {
        this.product = product;
        this.optionName = optionName;
        this.optionType = optionType;
        this.optionPrice = optionType == OptionType.INPUT ? optionPrice : null; // 선택형 옵션의 경우 가격 없음
        this.isActive = true;
    }

    /**
     * 옵션 정보 수정
     *
     * @param optionName 수정할 옵션 이름
     * @param optionType 수정할 옵션 타입
     * @param optionPrice 수정할 옵션 추가 금액 (입력형)
     * @param hasOptionOrder 해당 옵션이 주문된 상태인지 여부
     */
    public void update(String optionName, OptionType optionType, BigDecimal optionPrice, boolean hasOptionOrder) {
        if (!isUpdatable(hasOptionOrder)) {
            throw new CustomException(ErrorCode.OPTION_CANNOT_BE_UPDATED);
        }

        if (this.optionType != optionType) {
            log.info("옵션 타입 변경 - 기존 타입: {}, 새로운 타입: {}", this.optionType, optionType);
            deactivate(); // 기존 옵션 비활성화
        }

        this.optionName = optionName;
        this.optionType = optionType;
        this.optionPrice = optionType == OptionType.INPUT ? optionPrice : null;
    }

    /**
     * 옵션 활성화
     * - 활성화된 옵션 개수가 3개 이상이면 예외 발생
     * - 활성화 시, 상세 옵션도 함께 활성화됨
     *
     * @param activeOptionCount 현재 활성화된 옵션 개수
     */
    public void activate(int activeOptionCount) {
        if (activeOptionCount >= 3) {
            throw new CustomException(ErrorCode.OPTION_LIMIT_EXCEEDED);
        }
        this.isActive = true;
        optionDetails.forEach(OptionDetail::activate); // 상세 옵션도 함께 활성화
    }

    /**
     * 옵션 비활성화
     * - 비활성화 시, 상세 옵션도 함께 비활성화됨
     */
    public void deactivate() {
        this.isActive = false;
        optionDetails.forEach(OptionDetail::deactivate);
    }

    /**
     * 옵션 수정 가능 여부 확인
     * 1. 주문되지 않은 옵션만 수정 가능
     *
     * @param hasOptionOrder 옵션 주문 여부
     * @return 수정 가능 여부 (true: 수정 가능, false: 수정 불가)
     */
    public boolean isUpdatable(boolean hasOptionOrder) {
        boolean updatable = !hasOptionOrder;
        log.info("옵션 수정 가능 여부 확인 - hasOptionOrder: {}, updatable: {}", hasOptionOrder, updatable);
        return updatable;
    }

    /**
     * 옵션 삭제 가능 여부 확인
     * 1. 주문되지 않은 상품의 옵션 → 활성화 상태여도 삭제 가능
     * 2. 주문된 상품의 옵션 → 절대 삭제 불가능
     *
     * @param hasOptionOrder 옵션 주문 여부
     * @return 삭제 가능 여부 (true: 삭제 가능, false: 삭제 불가)
     */
    public boolean isDeletable(boolean hasOptionOrder) {
        boolean deletable = !hasOptionOrder;
        log.info("옵션 삭제 가능 여부 확인 - hasOptionOrder: {}, deletable: {}", hasOptionOrder, deletable);
        return deletable;
    }

}
