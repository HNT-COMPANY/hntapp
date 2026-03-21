package com.hnt.hntapp.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponseDto {

    private String email;
    private String name;
    private String role;
}
