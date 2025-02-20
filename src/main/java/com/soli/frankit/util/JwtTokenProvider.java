package com.soli.frankit.util;

import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
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

    private final Key key;
    private final long expirationTime;

    /**
     * JWT 설정값을 주입받아 초기화
     *
     * @param secret         Base64 인코딩된 JWT 서명용 키
     * @param expirationTime JWT 만료 시간 (밀리초)
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expirationTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    /**
     * JWT 토큰 생성
     *
     * @param email 사용자 이메일
     * @return 생성된 JWT 토큰
     */
    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                    .subject(email)
                    .issuedAt(now)
                    .expiration(expiryDate)
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
                   .setSigningKey(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token 검증할 JWT 토큰
     * @throws CustomException 토큰이 유효하지 않거나, 서명이 올바르지 않을 때 발생
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

}


