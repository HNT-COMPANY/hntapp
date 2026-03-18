package com.hnt.hntapp.config;


import com.hnt.hntapp.domain.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** 모든 API 요청마다
 "이 요청에 유효한 JWT 토큰이 있나?" 확인하는 역할이에요
 토큰 있으면 → 사용자 정보 Security Context에 저장
 토큰 없으면 → 다음 필터로 넘김 (로그인 API는 토큰 불필요)*/

/**
 * JWT 인증 필터
 * - 모든 API 요청마다 자동 실행
 * - Authorization 헤더에서 JWT 토큰 추출
 * - 토큰 유효성 검증 후 Security Context에 사용자 정보 저장
 * - OncePerRequestFilter: 요청당 딱 한번만 실행 보장
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    /** Jwt 토큰 생성/검증 유틸 */
    private final JwtUtil jwtUtil;

    /** DB에서 사용자 정보 조회 */
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterchain) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출
        // 형식: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");

        // 2. 토큰 없거나 Bearer로 시작 안하면 다음 필터로 넘김
        // (로그인 API는 토큰 없이 접근 가능)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterchain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 이후 실제 토큰 값 추출 하기
        String token = authHeader.substring(7);

        // 4. 토큰 유효성 검증
        if (!jwtUtil.validateToken(token)) {
            filterchain.doFilter(request, response);
            return;
        }

        // 5. 토큰에서 이메일 추출 과정
        String email = jwtUtil.getEmail(token);

        // 6. 이미 인증된 요청이면 스킵 한다.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // 7. DB에서 사용자 정보 저회 과정
            UserDetails userdetails = userDetailsService.loadUserByUsername(email);

            // 8. Security Context에 인증 정보 저장
            // -> 이후 @AuthenticationPrincipal 로 사용자 정보 꺼낼 수 있음
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userdetails, null, userdetails.getAuthorities());

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 9. 다음 필터로 넘김
        filterchain.doFilter(request, response);
    }

}
