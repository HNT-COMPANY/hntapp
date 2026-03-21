package com.hnt.hntapp.domain.user.controller;


import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.domain.user.dto.LoginRequestDto;
import com.hnt.hntapp.domain.user.dto.LoginResponseDto;
import com.hnt.hntapp.domain.user.dto.RegisterRequestDto;
import com.hnt.hntapp.domain.user.dto.RegisterResponseDto;
import com.hnt.hntapp.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러
 * - 로그인 API 엔드포인트 제공
 * - SecurityConfig에서 /api/auth/** 는 인증 없이 접근 가능
 *
 * [API 목록]
 * POST /api/auth/login → 로그인 (JWT 토큰 발급)
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /** 로그인 비즈니스 로직 처리 서비스 */
    private final AuthService authService;

    /**
     * 로그인 API
     * - 이메일 / 비밀번호 검증
     * - 성공 시 JWT 토큰 발급
     *
     * @param request 이메일, 비밀번호
     * @return JWT 토큰, 사용자 정보
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody LoginRequestDto request) {

        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
            @RequestBody RegisterRequestDto request ) {
        RegisterResponseDto response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", response));
    }


}
