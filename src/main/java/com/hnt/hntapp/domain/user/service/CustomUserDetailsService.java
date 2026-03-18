package com.hnt.hntapp.domain.user.service;


import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security UserDetailsService 구현체
 * - Spring Security가 로그인 시 자동으로 호출
 * - 이메일로 DB에서 사용자 조회
 * - 사용자 정보를 Spring Security가 이해할 수 있는 형태로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /** 사용자 DB 조회용 Repository */
    private final UserRepository userRepository;

    /**
     * 이메일로 사용자 조회
     * - Spring Security가 로ㅓ그인 시 자동 호출
     * - DB에서 사용자 찾아서 UserDetails 형태로 반환
     * - 사용자 없으면 UsernameNotFoundException 발생
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // DB에서 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다: " + email));

        //Spring Security UserDetails 형태로 변환하고 반환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                // 권한 설정 (ROLE_ 접두사 필수)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
