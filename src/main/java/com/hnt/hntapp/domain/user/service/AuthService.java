package com.hnt.hntapp.domain.user.service;

import com.hnt.hntapp.config.JwtUtil;
import com.hnt.hntapp.domain.user.dto.LoginRequestDto;
import com.hnt.hntapp.domain.user.dto.LoginResponseDto;
import com.hnt.hntapp.domain.user.dto.RegisterRequestDto;
import com.hnt.hntapp.domain.user.dto.RegisterResponseDto;
import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증 서비스
 * - 로그인 처리 담당
 * - 이메일/비밀번호 검증 후 JWT 토큰 발급
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /** 사용자 DB 조회 */
    private final UserRepository userRepository;

    /** 비밀번호 검증 (BCrypt) */
    private final PasswordEncoder passwordEncoder;

    /** JWT 토큰 생성 */
    private final JwtUtil jwtUtil;

    /**
     * 로그인 처리
     * 1. 이메일로 사용자 조회
     * 2. 비밀번호 검증
     * 3. 활성화 여부 확인
     * 4. JWT 토큰 발급
     * 5. 응답 데이터 반환
     */
    public LoginResponseDto login(LoginRequestDto request) {

        // 1. 이메일로 사용자 조회
        // 없으면 예외 발생
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "이메일 또는 비밀번호가 올바르지 않습니다."));

        // 2. 비밀번호 검증
        // 입력한 비밀번호와 DB에 저장된 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. 활성화 여부 확인
        // 탈퇴 또는 정지된 계정은 로그인 불가
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        // 4. JWT 토큰 발급
        // 이메일과 권한 정보를 토큰에 담아서 발급
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

        // 5. 응답 데이터 반환
        return LoginResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public RegisterResponseDto register(RegisterRequestDto request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이 이메일은 사용할 수 없습니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .name(request.getName())
                .role(Role.valueOf(request.getRole()))  // "ADMIN" or "OWNER"
                .isActive(true)
                .build();

        userRepository.save(user);
        // 4. 응답 반환
        return RegisterResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .role(user.getRole().name())
                .build();


    }

}
