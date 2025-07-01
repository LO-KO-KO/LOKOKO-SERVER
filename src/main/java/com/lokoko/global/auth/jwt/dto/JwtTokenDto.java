package com.lokoko.global.auth.jwt.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import lombok.Builder;

@Builder
public record JwtTokenDto(
        String accessToken,
        String refreshToken,
        OauthLoginStatus loginStatus
) {
    public static JwtTokenDto of(
            String accessToken,
            String refreshToken,
            OauthLoginStatus loginStatus
    ) {
        return new JwtTokenDto(accessToken, refreshToken, loginStatus);
    }

    public static JwtTokenDto of(String accessToken, String refreshToken) {
        return new JwtTokenDto(accessToken, refreshToken, OauthLoginStatus.LOGIN);
    }
}
