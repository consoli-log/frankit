package com.soli.frankit.util;

import com.soli.frankit.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * packageName  : com.soli.frankit.util
 * fileName     : JwtAuthenticationFilter
 * author       : eumsoli
 * date         : 2025-02-20
 * description  : API 요청 시 JWT 검증 및 인증 처리 필터
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 요청이 들어올 때마다 JWT 인증을 처리하는 필터
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 예외 발생 시 처리
     * @throws IOException      예외 발생 시 처리
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1️⃣ 요청에서 JWT 토큰 가져오기
            String token = resolveToken(request);

            // 2️⃣ 토큰이 존재하면 유효성 검증 후 사용자 정보 설정
            if (token != null && jwtTokenProvider.validateToken(token)) {
                setAuthentication(token, request);
            }

        } catch (CustomException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰 추출
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 (없다면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * JWT 토큰을 이용해 사용자 정보 설정
     *
     * @param token   유효한 JWT 토큰
     * @param request HTTP 요청 객체
     */
    private void setAuthentication(String token, HttpServletRequest request) {
        String email = jwtTokenProvider.extractEmail(token);

        User principal = new User(email, "", new java.util.ArrayList<>());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
