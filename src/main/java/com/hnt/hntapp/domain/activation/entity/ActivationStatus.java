package com.hnt.hntapp.domain.activation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivationStatus {
    DRAFT("임시저장"),
    SUBMITTED("제출"),
    APPROVED("승인"),
    REJECTED("보류");

    private final String label;

}
