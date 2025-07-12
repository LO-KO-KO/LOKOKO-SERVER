package com.lokoko.global.auth.jwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_TOKEN_HEADER = "AccessToken";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    public static final String ID_CLAIM = "id";
    public static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";
    private static final String ROLE_PREFIX = "ROLE_";
    public static final String LINE_ID_CLAIM = "lineId";

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

    public String generateAccessToken(Long userId, String role, String lineId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()
                        + accessTokenExpiration))
                .claim(ID_CLAIM, userId)
                .claim(ROLE_CLAIM, ROLE_PREFIX + role)
                .claim(LINE_ID_CLAIM, lineId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, String role, String tokenId, String lineId) {
        return Jwts.builder()
                .setId(tokenId)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()
                        + refreshTokenExpiration))
                .claim(ID_CLAIM, userId)
                .claim(ROLE_CLAIM, ROLE_PREFIX + role)
                .claim(LINE_ID_CLAIM, lineId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jwt toSpringJwt(String tokenValue) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(tokenValue);

        JwsHeader<?> jwsHeader = jws.getHeader();
        Claims jwsBody = jws.getBody();

        Instant issuedAt = dateToInstant(jwsBody.getIssuedAt());
        Instant expiresAt = dateToInstant(jwsBody.getExpiration());

        Map<String, Object> headers = new HashMap<>();
        jwsHeader.forEach(headers::put);

        Map<String, Object> claims = new HashMap<>();
        jwsBody.forEach(claims::put);

        return new Jwt(
                tokenValue,
                issuedAt,
                expiresAt,
                headers,
                claims
        );
    }

    private Instant dateToInstant(Date date) {
        return date != null
                ? date.toInstant()
                : Instant.EPOCH;
    }
}

