package com.hnt.hntapp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 유틸리티 클래스
 * - 로그인 성공 시 토큰 생성
 * - 요청마다 토큰 유효성 검증
 * - 토큰에서 사용자 정보 추출
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    /**
     * 생성자 주입
     * - @Value로 application.yml의 jwt.secret, jwt.expiration 값 읽어옴
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        // 비밀키를 HMAC-SHA 알고리즘용 SecretKey로 변환
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * JWT 토큰 생성
     * - 로그인 성공 시 호출
     * - 이메일과 권한(role)을 토큰 안에 담아서 발급
     */
    public String generateToken(String email, String role){
        return Jwts.builder()
                .subject(email) // 토큰 주인 ( 이메일 )
                .claim("role", role) // 권한 정보 ( ADMIN / FRANCHISEE / STAFF )
                .issuedAt(new Date()) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간 (24시간)
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    /**
     * 토큰에서 이메일 추출
     * - API 요청 시 누가 요청했는지 확인할 때 사용
     */
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰에서 권한 추출
     * - ADMIN/FRANCHISEE/STAFF 구분할 때 사용
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }
    /**
     * 토큰 유효성 검증
     * - 만료 / 변조 / 잘못된 형식 체크
     * - true: 정상 토큰
     * - false: 유효하지 않은 토큰
     */
    public boolean validateToken(String token){
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e ) {
            return false;
        }
    }

    /**
     * 토큰 파싱
     * - 비밀키로 서명 검증 후 Claims(토큰 내용) 반환
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }





}
