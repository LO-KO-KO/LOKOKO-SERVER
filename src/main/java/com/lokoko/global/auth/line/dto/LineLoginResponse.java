package com.lokoko.global.auth.line.dto;

import com.lokoko.global.auth.jwt.dto.LoginDto;

public record LineLoginResponse(
        LoginDto tokens
) {
}
