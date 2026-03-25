package com.hnt.hntapp.domain.activation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Carrier {

    SK("SK텔레콤"),
    KT("KT"),
    LG("LG U+");

    private final String label;

    // 거래처명으로 통신사 자동 구분
    public static Carrier fromDealer(String dealerName) {
        if (dealerName != null) {
            String d = dealerName.trim();
            if (d.equals("라온") || d.equals("한빛") || d.equals("예스") || d.equals("PS")) {
                return SK;
            }
            // KT 거래처
            if (d.equals("에이딘") || d.equals("M&S") || d.equals("오텔")) {
                return KT;
            }
            // LG 거래처
            if (d.equals("메타") || d.equals("수석") || d.equals("빅뱅") || d.equals("골드스타") || d.equals("모스트") || d.equals("수인") || d.equals("가본")) {
                return LG;
            }
        }
            throw new IllegalArgumentException("알 수 없는 거래처입니다: " + dealerName);
        }
}
