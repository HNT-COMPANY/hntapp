package com.hnt.hntapp.domain.user.dto;

import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.entity.UserStatus;
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
    private UserStatus status;
    // 소속 가맹점 명 ( admin이면 본사 )
    private String franchiseName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private FranchiseInfo franchise;

    @Getter
    @Builder
    public static class FranchiseInfo {
        private UUID   id;
        private String name;
        private String region;
    }

    // user entity -> DTO 변환
    public static UserResponseDto from(User user) {
        FranchiseInfo franchiseInfo = null;
        if (user.getFranchise() != null) {
            franchiseInfo = FranchiseInfo.builder()
                    .id(user.getFranchise().getId())
                    .name(user.getFranchise().getName())
                    .region(user.getFranchise().getRegion())
                    .build();
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .isActive(user.getIsActive())
                .franchiseName(
                        user.getFranchise() != null
                        ? user.getFranchise().getName()
                        : "본사"
                )
                .franchise(franchiseInfo)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
