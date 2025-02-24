package com.soli.frankit.repository;

import com.soli.frankit.entity.OptionDetail;
import com.soli.frankit.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName  : com.soli.frankit.repository
 * fileName     : OptionDetailRepository
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : 상세 옵션 정보를 관리하는 JPA Repository
 */
@Repository
public interface OptionDetailRepository extends JpaRepository<OptionDetail, Long> {

    /**
     * 특정 옵션의 모든 상세 옵션 조회
     *
     * @param productOption 특정 옵션
     * @return 옵션에 속한 상세 옵션 목록
     */
    List<OptionDetail> findByProductOption(ProductOption productOption);

    /**
     * 특정 옵션의 활성화된 상세 옵션 개수 조회
     *
     * @param productOption 특정 옵션
     * @param isActive 옵션 활성화 여부
     * @return 활성화된 옵션 개수
     */
    long countByProductOptionAndIsActiveTrue(ProductOption productOption, boolean isActive);

    /**
     * 특정 옵션 개수 조회
     *
     * @param productOption 특정 옵션
     * @return 활성화된 옵션 개수
     */
    long countByProductOption(ProductOption productOption);

    /**
     * 특정 옵션의 활성화된 상세 옵션 목록 조회
     *
     * @param productOption 특정 옵션
     * @return 활성화된 상세 옵션 목록
     */
    List<OptionDetail> findByProductOptionAndIsActiveTrue(ProductOption productOption);

}
