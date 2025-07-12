package com.lokoko.global.auth.controller;

import com.lokoko.global.auth.jwt.dto.JwtTokenDto;

public record JwtLoginResponse(
        String accessToken,
        String refreshToken
) {
    public static JwtLoginResponse of(JwtTokenDto dto) {
        return new JwtLoginResponse(dto.accessToken(), dto.refreshToken());
    }
}
