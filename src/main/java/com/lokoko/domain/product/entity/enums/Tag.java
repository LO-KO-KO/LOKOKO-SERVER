package com.lokoko.domain.product.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tag {
    BEST("베스트"),
    NEW("신상품");

    private final String displayName;
}
