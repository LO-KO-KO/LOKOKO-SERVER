package com.lokoko.global.auth.jwt.service;

import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
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
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";
    private static final String KEY_DELIMITER = ":";
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Value("${lokoko.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    public JwtTokenDto generateJwtToken(GenerateTokenDto dto) {
        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtProvider.generateAccessToken(dto.id(), dto.role(), dto.lineId());
        String refreshToken = jwtProvider.generateRefreshToken(dto.id(), dto.role(), tokenId, dto.lineId());

        String redisKey = "refreshToken:" + dto.id() + ":" + tokenId;
        redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

        return JwtTokenDto.of(accessToken, refreshToken, tokenId);
    }

    public JwtTokenDto reissueJwtToken(String refreshToken) {
        Long userId = jwtExtractor.getId(refreshToken);
        String tokenId = jwtExtractor.getTokenId(refreshToken);
        String redisKey = REFRESH_TOKEN_KEY_PREFIX + userId + KEY_DELIMITER + tokenId;
        String stored = redisUtil.getRefreshToken(redisKey);

        if (!MessageDigest.isEqual(
                stored.getBytes(StandardCharsets.UTF_8),
                refreshToken.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new TokenInvalidException();
        }

        if (jwtExtractor.isExpired(refreshToken)) {
            throw new TokenExpiredException();
        }

        String role = jwtExtractor.getRole(refreshToken);
        String email = jwtExtractor.getLineId(refreshToken);
        JwtTokenDto newTokens = generateJwtToken(GenerateTokenDto.of(userId, role, email));

        redisUtil.deleteRefreshToken(redisKey);
        return newTokens;
    }

    public JwtTokenDto issueTokensForTest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String tokenId = UUID.randomUUID().toString();
        String fakeLineId = user.getId().toString();

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(),
                user.getRole().name(),
                fakeLineId
        );
        String refreshToken = jwtProvider.generateRefreshToken(
                user.getId(),
                user.getRole().name(),
                tokenId,
                fakeLineId
        );

        return JwtTokenDto.of(accessToken, refreshToken, tokenId);
    }
}
