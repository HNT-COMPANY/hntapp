package com.hnt.hntapp.domain.audit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuditType {
    FIRST("1차 검수"),
    SECOND("2차 검수");

    private final String label;
}
