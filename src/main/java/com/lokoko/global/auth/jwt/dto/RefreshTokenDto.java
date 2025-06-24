package com.lokoko.global.auth.jwt.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenDto(
        @NotNull String refreshToken
) {
}
