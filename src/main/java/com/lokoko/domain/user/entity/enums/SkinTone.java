package com.lokoko.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinTone {
    BELOW_17("17호 미만"),
    FROM_17_TO_19("17-19호"),
    FROM_19_TO_21("19-21호"),
    FROM_21_TO_23("21-23호"),
    ABOVE_23("23호 초과");

    private final String displayName;
}
