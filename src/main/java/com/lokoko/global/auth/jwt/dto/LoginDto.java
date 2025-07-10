package com.lokoko.global.auth.jwt.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import lombok.Builder;

@Builder
public record LoginDto(
        String accessToken,
        String refreshToken,
        OauthLoginStatus loginStatus,
        Long userId,
        String tokenId
) {
    public static LoginDto of(
            String accessToken,
            String refreshToken,
            OauthLoginStatus loginStatus,
            Long userId,
            String tokenId
    ) {
        return new LoginDto(accessToken, refreshToken, loginStatus, userId, tokenId);
    }
}
