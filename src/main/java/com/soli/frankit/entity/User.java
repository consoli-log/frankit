package com.soli.frankit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * packageName  : com.soli.frankit.entity
 * fileName     : User
 * author       : eumsoli
 * date         : 2025-02-17
 * description  : 사용자 정보를 저장하는 엔티티
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long id; // 사용자 ID

    @Column(unique = true, nullable = false, length = 100)
    private String email; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 등록일

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    /**
     * User 생성자
     *
     * @param email    사용자 이메일
     * @param password 암호화된 비밀번호
     */
    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * 비밀번호 변경 메서드
     *
     * @param newPassword 새로운 비밀번호
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

}
