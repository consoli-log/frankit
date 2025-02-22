package com.soli.frankit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

}
