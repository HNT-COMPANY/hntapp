package com.hnt.hntapp.domain.user.service;

import com.hnt.hntapp.config.JwtUtil;
import com.hnt.hntapp.domain.user.dto.LoginRequestDto;
import com.hnt.hntapp.domain.user.dto.LoginResponseDto;
import com.hnt.hntapp.domain.user.dto.RegisterRequestDto;
import com.hnt.hntapp.domain.user.dto.RegisterResponseDto;
import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.entity.UserStatus;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * AuthService
 * - 로그인 / 회원가입 처리
 *
 * [변경 내역]
 * - login(): LoginResponseDto 빌더에 .franchiseId(franchiseId) 누락 수정
 * - System.out.println() 로그 추가 (흐름 추적용)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto request) {
        System.out.println("[AuthService] login() 호출 - email=" + request.getEmail());

        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("[AuthService] 사용자 없음 - email=" + request.getEmail());
                    return new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
                });

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            System.out.println("[AuthService] 비밀번호 불일치 - email=" + request.getEmail());
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. 활성화 여부 확인
        if (!user.getIsActive()) {
            System.out.println("[AuthService] 비활성화 계정 - email=" + request.getEmail());
            throw new IllegalArgumentException("현재 계정이 비활성화 상태입니다. 관리자에게 문의하세요.");
        }

        // 4. 가입 심사 확인
        if (user.getStatus() == UserStatus.PENDING) {
            System.out.println("[AuthService] 승인 대기 계정 - email=" + request.getEmail());
            throw new IllegalArgumentException("가입 승인 대기 중입니다. 관리자에게 문의하세요.");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            System.out.println("[AuthService] 반려된 계정 - email=" + request.getEmail());
            throw new IllegalArgumentException("가입이 반려되었습니다. 관리자에게 문의하세요.");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            System.out.println("[AuthService] 비활성화 계정(INACTIVE) - email=" + request.getEmail());
            throw new IllegalArgumentException("현재 계정이 비활성화 상태입니다. 관리자에게 문의하세요");
        }

        // 5. JWT 토큰 발급
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());
        System.out.println("[AuthService] JWT 토큰 발급 완료 - role=" + user.getRole().name());

        // 6. 가맹점 ID 추출
        UUID franchiseId = (user.getFranchise() != null)
                ? user.getFranchise().getId()
                : null;
        System.out.println("[AuthService] franchiseId=" + franchiseId);

        // 7. 응답 반환
        // [수정] franchiseId 누락 → .franchiseId(franchiseId) 추가
        return LoginResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .franchiseId(franchiseId)   // ← 이 줄이 누락되어 있었음
                .build();
    }

    public RegisterResponseDto register(RegisterRequestDto request) {
        System.out.println("[AuthService] register() 호출 - email=" + request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("[AuthService] 중복 이메일 - email=" + request.getEmail());
            throw new IllegalArgumentException("이 이메일은 사용할 수 없습니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .role(Role.FRANCHISEE)
                .status(UserStatus.PENDING)
                .isActive(true)
                .build();

        userRepository.save(user);
        System.out.println("[AuthService] 회원가입 완료 - email=" + user.getEmail());

        return RegisterResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
