package com.hnt.hntapp.domain.warehouse.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UnitStatus {
    STOCK("입고"),
    SOLD("판매"),
    LOST("분실"),
    TRANSFERRED("이관");

    private final String label;
}
