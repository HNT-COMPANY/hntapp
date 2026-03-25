package com.hnt.hntapp.domain.activation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivationType {
    NEW("신규"),
    CHANGE("기기변경"),
    MNP("번호이동");


    private final String label;
}
