package com.lokoko.global.auth.line.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.jwt.dto.LoginDto;

public record LineLoginResponse(
        OauthLoginStatus loginStatus
) {
    public static LineLoginResponse of(
            OauthLoginStatus loginStatus
    ) {
        return new LineLoginResponse(loginStatus);
    }

    public static LineLoginResponse from(LoginDto loginDto) {
        return new LineLoginResponse(
                loginDto.loginStatus()
        );
    }
}
