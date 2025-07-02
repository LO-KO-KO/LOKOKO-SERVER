package com.lokoko.global.auth.line.dto;

public record LineProfileResponse(
        String userId,
        String displayName,
        String pictureUrl,
        String statusMessage,
        String email
) {
}
