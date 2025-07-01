package com.lokoko.global.auth.jwt.service;

import com.lokoko.global.auth.jwt.dto.GenerateTokenDto;
import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.jwt.exception.TokenExpiredException;
import com.lokoko.global.auth.jwt.exception.TokenInvalidException;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.utils.RedisUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final RedisUtil redisUtil;

    @Value("${lokoko.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public JwtTokenDto generateJwtToken(GenerateTokenDto dto) {
        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtProvider.generateAccessToken(dto.id(), dto.role());
        String refreshToken = jwtProvider.generateRefreshToken(dto.id(), dto.role(), tokenId);

        String redisKey = "refreshToken:" + dto.id() + ":" + tokenId;
        redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

        return JwtTokenDto.of(accessToken, refreshToken, tokenId);
    }

    public JwtTokenDto reissueJwtToken(String refreshToken) {
        if (jwtExtractor.isExpired(refreshToken)) {
            throw new TokenExpiredException();
        }

        Long userId = jwtExtractor.getId(refreshToken);
        String tokenId = jwtExtractor.getTokenId(refreshToken);
        String redisKey = "refreshToken:" + userId + ":" + tokenId;
        String stored = redisUtil.getRefreshToken(redisKey);
        if (!MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                refreshToken.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new TokenInvalidException();
        }

        redisUtil.deleteRefreshToken(redisKey);

        String role = jwtExtractor.getRole(refreshToken);
        return generateJwtToken(GenerateTokenDto.of(userId, role));
    }
}
