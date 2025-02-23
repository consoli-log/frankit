package com.soli.frankit.repository;

import com.soli.frankit.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName  : com.soli.frankit.repository
 * fileName     : ProductOptionRepository
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 옵션 정보를 관리하는 JPA Repository
 */
@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    /**
     * 특정 상품의 모든 옵션 조회
     *
     * @param productId 상품 ID
     * @return 상품에 속한 옵션 목록
     */
    List<ProductOption> findByProductId(Long productId);

    /**
     * 특정 상품의 활성화된 옵션 개수 조회
     *
     * @param productId 상품 ID
     * @param isActive 옵션 활성화 여부
     * @return 활성화된 옵션 개수
     */
    long countByProductIdAndIsActive(Long productId, boolean isActive);

    /**
     * 특정 상품의 활성화된 옵션 목록 조회
     *
     * @param productId 상품 ID
     * @return 활성화된 옵션 목록
     */
    List<ProductOption> findByProductIdAndIsActiveTrue(Long productId);

}
