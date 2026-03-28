package com.hnt.hntapp.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {

    /** 발급된 JWT 토큰 (Bearer 토큰으로 사용) */
    private String token;

    /** 사용자 이메일 */
    private String email;

    /** 사용자 이름 */
    private String name;

    /**
     * 사용자 권한
     * - ADMIN / FRANCHISEE / STAFF /CUSTOMER
     */
    private String role;

    private UUID franchiseId;
}
