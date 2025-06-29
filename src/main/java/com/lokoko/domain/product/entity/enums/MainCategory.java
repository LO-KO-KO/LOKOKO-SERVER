package com.lokoko.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MainCategory {
    SKIN_CARE("스킨케어", "1000000008"),
    MAKEUP("메이크업", "1000000031");

    private final String displayName;
    private final String ctgrNo;
}
