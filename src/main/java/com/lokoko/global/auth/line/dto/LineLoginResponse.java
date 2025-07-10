package com.lokoko.global.auth.line.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.jwt.dto.LoginDto;

public record LineLoginResponse(
        String accessToken,
        String refreshToken,
        OauthLoginStatus loginStatus
) {
    public static LineLoginResponse of(
            String accessToken,
            String refreshToken,
            OauthLoginStatus loginStatus
    ) {
        return new LineLoginResponse(accessToken, refreshToken, loginStatus);
    }

    public static LineLoginResponse from(LoginDto loginDto) {
        return new LineLoginResponse(
                loginDto.accessToken(),
                loginDto.refreshToken(),
                loginDto.loginStatus()
        );
    }
}
