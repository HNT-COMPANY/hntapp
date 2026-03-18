package com.hnt.hntapp.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 * - 클라이언트에서 로그인 시 전송하는 데이터
 * - 이메일 + 비밀번호
 */
@Getter
@NoArgsConstructor
public class LoginRequestDto {

    /** 로그인 이메일 */
    private String email;

    /** 로그인 비밀번호 (평문, 서버에서 암호화 검증) */
    private String password;
}
