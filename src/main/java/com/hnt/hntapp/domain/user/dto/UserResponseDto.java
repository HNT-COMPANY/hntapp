package com.hnt.hntapp.domain.user.dto;

import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponseDto {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private Role role;
    private Boolean isActive;

    // 소속 가맹점 명 ( admin이면 본사 )
    private String franchiseName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // user entity -> DTO 변환
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .franchiseName(
                        user.getFranchise() != null
                        ? user.getFranchise().getName()
                        : "본사"
                )
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
