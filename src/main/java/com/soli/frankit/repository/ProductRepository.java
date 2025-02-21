package com.soli.frankit.repository;

import com.soli.frankit.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName  : com.soli.frankit.repository
 * fileName     : ProductRepository
 * author       : eumsoli
 * date         : 2025-02-21
 * description  : 상품 정보를 관리하는 JPA Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
