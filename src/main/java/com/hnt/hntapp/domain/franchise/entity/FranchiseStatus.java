package com.hnt.hntapp.domain.franchise.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FranchiseStatus {

    OPERATING("운영중"),
    CLOSED("폐업"),
    SUSPENDED("휴업");

    private final String label;

    public static FranchiseStatus from(String value) {
        for (FranchiseStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)
                    || status.label.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(
                "유효하지 않은 상태값입니다: " + value
                        + " (허용값: OPERATING, CLOSED, SUSPENDED)");
    }
}
