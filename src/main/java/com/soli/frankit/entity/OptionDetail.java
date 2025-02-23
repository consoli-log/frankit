package com.soli.frankit.entity;

import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * packageName  : com.soli.frankit.entity
 * fileName     : OptionDetail
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상세 옵션 정보를 저장하는 엔티티
 */

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "option_details")
public class OptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_seq")
    private Long id; // 상세 옵션 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_seq")
    private ProductOption productOption; // 연결된 상품 옵션

    @Column(nullable = false)
    private String detailName; // 상세 옵션 이름

    @Column(nullable = false)
    private BigDecimal detailPrice; // 상세 옵션 추가 금액

    @Column(nullable = false)
    private boolean isActive = true; // 활성화 여부 (기본값 true)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    /**
     * OptionDetail 생성자
     *
     * @param productOption 연결된 옵션
     * @param detailName 상세 옵션명
     * @param detailPrice 추가 금액
     */
    @Builder
    public OptionDetail(ProductOption productOption, String detailName, BigDecimal detailPrice) {
        this.productOption = productOption;
        this.detailName = detailName;
        this.detailPrice = detailPrice;
        this.isActive = true;
    }

    /**
     * 상세 옵션 정보 수정
     *
     * @param detailName 수정할 상세 옵션명
     * @param detailPrice 수정할 추가 금액
     * @param hasDetailOrder 해당 상세 옵션이 주문된 적 있는지 여부
     */
    public void update(String detailName, BigDecimal detailPrice, boolean hasDetailOrder) {
        if (!isUpdatable(hasDetailOrder)) {
            throw new CustomException(ErrorCode.OPTION_DETAIL_CANNOT_BE_UPDATED);
        }
        this.detailName = detailName;
        this.detailPrice = detailPrice;
    }

    /**
     * 상세 옵션 활성화
     * - 옵션이 비활성화된 경우, 활성화 불가능
     */
    public void activate() {
        if (!productOption.isActive()) {
            throw new CustomException(ErrorCode.OPTION_DETAIL_CANNOT_BE_ACTIVATED);
        }
        this.isActive = true;
    }

    /**
     * 상세 옵션 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 상세 옵션 삭제 가능 여부 확인
     * 1. 주문되지 않은 옵션만 삭제 가능
     *
     * @param hasDetailOrder 상세 옵션 주문 여부
     * @return 삭제 가능 여부 (true: 삭제 가능, false: 삭제 불가)
     */
    public boolean isDeletable(boolean hasDetailOrder) {
        boolean deletable = !hasDetailOrder;
        log.info("상세 옵션 삭제 가능 여부 확인 - hasDetailOrder: {}, deletable: {}", hasDetailOrder, deletable);
        return deletable;
    }

    /**
     * 상세 옵션 수정 가능 여부 확인
     * 1. 주문되지 않은 상세 옵션만 수정 가능
     *
     * @param hasDetailOrder 상세 옵션 주문 여부
     * @return 수정 가능 여부 (true: 수정 가능, false: 수정 불가)
     */
    public boolean isUpdatable(boolean hasDetailOrder) {
        boolean updatable = !hasDetailOrder;
        log.info("상세 옵션 수정 가능 여부 확인 - hasDetailOrder: {}, updatable: {}", hasDetailOrder, updatable);
        return updatable;
    }

}
