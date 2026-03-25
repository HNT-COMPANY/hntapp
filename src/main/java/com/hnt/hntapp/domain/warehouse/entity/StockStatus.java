package com.hnt.hntapp.domain.warehouse.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockStatus {
    NORMAL("정상"),
    LOW("부족"),
    OUT_OF_STOCK("품절");

    private final String label;
}
