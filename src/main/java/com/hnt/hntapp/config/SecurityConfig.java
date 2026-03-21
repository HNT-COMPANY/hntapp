package com.hnt.hntapp.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * - JWT 기반 인증 설정
 * - API별 접근 권한 설정
 * - 비밀번호 암호화 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** JWT 인증 필터 */
    private final JwtFilter jwtFilter;

    /**
     * 비밀번호 암호화 설정
     * - BCrypt 알고리즘 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정
     * - 로그인 시 이메일/비밀번호 검증 담당
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security 필터 체인 설정
     * - CSRF 비활성화
     * - 세션 비활성화 (JWT Stateless)
     * - API별 접근 권한 설정
     * - JWT 필터 등록
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // API별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인은 누구나 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // 가맹점 관리는 ADMIN만
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 정산/정책은 ADMIN, FRANCHISEE
                        .requestMatchers("/api/franchisee/**")
                        .hasAnyRole("ADMIN", "FRANCHISEE")
                        // 출퇴근은 전체 직원
                        .requestMatchers("/api/attendance/**")
                        .hasAnyRole("ADMIN", "FRANCHISEE", "STAFF")
                        // 나머지 로그인 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 등록
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
