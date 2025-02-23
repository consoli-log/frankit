package com.soli.frankit.service;

import org.springframework.stereotype.Service;

/**
 * packageName  : com.soli.frankit.service
 * fileName     : OrderService
 * author       : eumsoli
 * date         : 2025-02-23
 * description  : 상품 주문 관리를 담당하는 서비스 클래스 (임시)
 */
@Service
public class OrderService {

    /**
     * 상품이 주문된 적이 있는지 확인
     *
     * @param id 상품 ID
     * @return 주문이 존재하면 true, 없으면 false 반환
     */
    public boolean hasOrders(Long id) {
        // 실제 주문 여부를 확인하는 로직이 들어갈 예정 (예: OrderRepository 활용)
        return false; // 기본적으로 주문 없음 처리
    }

}
