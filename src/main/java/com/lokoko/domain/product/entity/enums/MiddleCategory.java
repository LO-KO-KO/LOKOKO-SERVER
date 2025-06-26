package com.lokoko.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MiddleCategory {

    // ── 스킨케어
    FACIAL_CARE("페이셜케어", MainCategory.SKIN_CARE, "1000000009"),

    // ── 메이크업
    FACE_MAKEUP("얼굴 메이크업", MainCategory.MAKEUP, "1000000032"),
    EYE_MAKEUP("아이 메이크업", MainCategory.MAKEUP, "1000000040"),
    LIP_MAKEUP("립 메이크업", MainCategory.MAKEUP, "1000000045");

    private final String displayName;
    private final MainCategory parent;
    private final String ctgrNo;
}
