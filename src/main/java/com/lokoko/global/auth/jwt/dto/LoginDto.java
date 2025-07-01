package com.lokoko.global.auth.jwt.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import lombok.Builder;

@Builder
public record LoginDto(
        String accessToken,
        String refreshToken,
        OauthLoginStatus loginStatus
) {
    public static LoginDto of(
            String accessToken,
            String refreshToken,
            OauthLoginStatus loginStatus
    ) {
        return new LoginDto(accessToken, refreshToken, loginStatus);
    }
}
