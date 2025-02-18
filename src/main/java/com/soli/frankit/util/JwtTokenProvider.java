package com.soli.frankit.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * packageName  : com.soli.frankit.util
 * fileName     : JwtTokenProvider
 * author       : eumsoli
 * date         : 2025-02-18
 * description  : JWT 토큰 생성 및 검증을 담당하는 클래스
 */

@Slf4j
@Component
public class JwtTokenProvider {

    private SecretKey key;
    private final long expirationTime;
    private final String secret;

    /**
     * JWT 설정값을 주입받아 초기화
     *
     * @param secret         Base64 인코딩된 JWT 서명용 키
     * @param expirationTime JWT 만료 시간 (밀리초)
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expirationTime) {
        this.secret = secret;
        this.expirationTime = expirationTime;
    }

    /**
     * SecretKey 초기화 (Base64 디코딩 후 키 생성)
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰 생성
     *
     * @param email 사용자 이메일
     * @return JWT 토큰
     */
    public String createToken(String email) {
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(accessExpiration)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰에서 이메일 추출
     *
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 토큰이 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.info("JWT Token is empty or null"); // 빈 토큰
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e); // 잘못된 토큰
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e); // 토큰 만료
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e); // 지원하지 않는 토큰
        }

        return false;
    }

}


