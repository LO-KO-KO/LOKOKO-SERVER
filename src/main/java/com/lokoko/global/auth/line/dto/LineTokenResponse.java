package com.lokoko.global.auth.line.dto;

public record LineTokenResponse(
        String access_token,
        String refresh_token,
        Long expires_in,
        String id_token
) {
}
