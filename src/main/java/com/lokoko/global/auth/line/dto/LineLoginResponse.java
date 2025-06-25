package com.lokoko.global.auth.line.dto;

import com.lokoko.global.auth.jwt.dto.JwtTokenDto;

public record LineLoginResponse(
        JwtTokenDto tokens
) {
}
