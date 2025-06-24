package com.lokoko.global.auth.jwt.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    private static final String ID_CLAIM = "id";
    private static final String ROLE_CLAIM = "role";
    private static final String ROLE_PREFIX = "ROLE_";

    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${lokoko.jwt.key}") String secretKey,
            @Value("${lokoko.jwt.access.expiration}") long accessTokenExpiration,
            @Value("${lokoko.jwt.refresh.expiration}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Long userId, String role) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(AUTHORIZATION_HEADER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .claim(ID_CLAIM, userId)
                .claim(ROLE_CLAIM, ROLE_PREFIX + role);

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, String role) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(REFRESH_TOKEN_HEADER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .claim(ID_CLAIM, userId)
                .claim(ROLE_CLAIM, ROLE_PREFIX + role);

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

