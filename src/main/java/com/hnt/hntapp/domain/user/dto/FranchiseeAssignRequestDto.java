package com.hnt.hntapp.domain.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FranchiseeAssignRequestDto(
        @NotNull(message = "가맹점 ID는 필수입니다.")
        UUID franchiseId) {

}
