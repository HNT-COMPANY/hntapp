package com.hnt.hntapp.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

    // 수정할 이름 값이 없으면 변경 안됨
    private String name;
    // 수정할 연락처 값 없으면 변경 x
    private String phone;
    private String role;
    private Boolean isActive;
    private UUID franchiseId;
}
