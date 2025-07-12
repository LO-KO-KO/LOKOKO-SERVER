package com.lokoko.global.auth.jwt.dto;

import lombok.Builder;

@Builder
public record JwtTokenDto(
        String accessToken,
        String refreshToken,
        String tokenId
) {
    public static JwtTokenDto of(String accessToken, String refreshToken, String tokenId) {
        return new JwtTokenDto(accessToken, refreshToken, tokenId);
    }

    public static JwtTokenDto of(String accessToken, String refreshToken) {
        return new JwtTokenDto(accessToken, refreshToken, null);
    }
}
