package com.lokoko.global.auth.jwt.dto;

import lombok.Builder;

@Builder
public record GenerateTokenDto(
        Long id,
        String role,
        String lineId
) {
    public static GenerateTokenDto of(Long id, String role, String lineId) {
        return new GenerateTokenDto(id, role, lineId);
    }

    public static GenerateTokenDto from(Long id, String role, String lineId) {
        return new GenerateTokenDto(id, role, lineId);
    }

    public static GenerateTokenDto from(GenerateTokenDto dto) {
        return new GenerateTokenDto(dto.id(), dto.role(), dto.lineId());
    }
}