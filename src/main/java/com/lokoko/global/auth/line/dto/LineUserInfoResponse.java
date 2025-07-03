package com.lokoko.global.auth.line.dto;

public record LineUserInfoResponse(
        String sub,
        String name,
        String picture
) {
}
