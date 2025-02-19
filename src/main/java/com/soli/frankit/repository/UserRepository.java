package com.soli.frankit.repository;

import com.soli.frankit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName  : com.soli.frankit.repository
 * fileName     : UserRepository
 * author       : eumsoli
 * date         : 2025-02-18
 * description  : 사용자 정보를 관리하는 JPA Repository
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일을 기반으로 사용자 정보를 조회
     *
     * @param email 사용자 이메일
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findByEmail(String email);

}
