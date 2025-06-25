package com.lokoko.domain.user.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    private final String displayName;
}
