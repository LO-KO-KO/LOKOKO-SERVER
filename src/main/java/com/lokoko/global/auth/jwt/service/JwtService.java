package com.lokoko.global.auth.jwt.service;

import com.lokoko.global.auth.jwt.dto.GenerateTokenDto;
import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.jwt.exception.CookieNotFoundException;
import com.lokoko.global.auth.jwt.exception.TokenExpiredException;
import com.lokoko.global.auth.jwt.exception.TokenInvalidException;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.utils.RedisUtil;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final RedisUtil redisUtil;

    private static Cookie[] getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CookieNotFoundException();
        }
        return cookies;
    }

    public JwtTokenDto generateJwtToken(GenerateTokenDto dto) {
        String accessToken = jwtProvider.generateAccessToken(dto.id(), dto.role());
        String refreshToken = jwtProvider.generateRefreshToken(dto.id(), dto.role());

        redisUtil.setRefreshToken(dto.id(), refreshToken);
        return JwtTokenDto.of(accessToken, refreshToken);
    }

    public JwtTokenDto reissueJwtToken(String refreshToken) {
        if (jwtExtractor.isExpired(refreshToken)) {
            throw new TokenExpiredException();
        }

        Long userId = jwtExtractor.getId(refreshToken);
        String redisToken = (String) redisUtil.getRefreshToken(userId);
        if (!redisToken.equals(refreshToken)) {
            throw new TokenInvalidException();
        }

        String role = jwtExtractor.getRole(refreshToken);
        return generateJwtToken(GenerateTokenDto.of(userId, role));
    }
}
