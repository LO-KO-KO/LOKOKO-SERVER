package com.lokoko.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinType {
    NORMAL("건성"),
    OILY("지성"),
    COMBINATION("복합성"),
    SENSITIVE("아토피성"),
    AGING("안정성"),
    OTHER("기타");

    private final String displayName;
}
