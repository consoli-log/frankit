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

/**
 * packageName  : com.soli.frankit.entity
 * fileName     : Product
 * author       : eumsoli
 * date         : 2025-02-20
 * description  : 상품 정보를 저장하는 엔티티
 */
@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_seq")
    private Long id; // 상품 ID

    @Column(nullable = false)
    private String name; // 상품 이름

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 상품 설명

    @Column(nullable = false)
    private BigDecimal price; // 상품 가격

    @Column(name = "shipping_fee", nullable = false)
    private BigDecimal shippingFee; // 배송비

    @Column(nullable = false)
    private boolean isActive = true; // 상품 활성화 여부 (기본값 true)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    /**
     * Product 생성자
     *
     * @param name 상품명
     * @param description 상품 설명
     * @param price 상품 가격
     * @param shippingFee 배송비
     */
    @Builder
    public Product(String name, String description, BigDecimal price, BigDecimal shippingFee) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.shippingFee = shippingFee;
        this.isActive = true; // 기본값 true (활성화 상태)
    }

    /**
     * 상품 정보 수정
     *
     * @param name 상품명
     * @param description 상품 설명
     * @param price 상품 가격
     * @param shippingFee 배송비
     */
    public void update(String name, String description, BigDecimal price, BigDecimal shippingFee) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.shippingFee = shippingFee;
    }

    /**
     * 상품 활성화
     */
    public void activate() {
        if (this.isActive) {
            throw new CustomException(ErrorCode.PRODUCT_ALREADY_ACTIVE);
        }
        this.isActive = true;
    }

    /**
     * 상품 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 상품이 삭제 가능한지 확인
     * 1. 주문되지 않은 상품 → 활성화 상태여도 삭제 가능
     * 2. 주문된 상품 → 절대 삭제 불가능
     *
     * @param hasOrder 주문 여부
     * @return 삭제 가능 여부 (true: 삭제 가능, false: 삭제 불가)
     */
    public boolean isDeletable(boolean hasOrder) {
        boolean deletable = !hasOrder;
        log.info("상품 삭제 가능 여부 확인 - 주문 여부: {}, 삭제 가능 여부: {}", hasOrder, deletable);
        return deletable;
    }


}
