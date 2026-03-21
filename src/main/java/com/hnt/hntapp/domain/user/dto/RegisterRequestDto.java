package com.hnt.hntapp.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequestDto {

    private String email;
    private String password;
    private String name;
    private String phone;
}
