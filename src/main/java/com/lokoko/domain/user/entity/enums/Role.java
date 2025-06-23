package com.lokoko.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("일반 유저"),
    ADMIN("관리자");

    private final String displayName;
}
