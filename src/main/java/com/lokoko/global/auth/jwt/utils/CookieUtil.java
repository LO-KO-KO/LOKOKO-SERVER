package com.lokoko.global.auth.jwt.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${lokoko.jwt.cookieMaxAge}")
    private Long cookieMaxAge;

    @Value("${lokoko.jwt.secureOption}")
    private boolean secureOption;

    @Value("${lokoko.jwt.cookiePathOption}")
    private String cookiePathOption;

    public void setCookie(String name, String value, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(cookieMaxAge)
                .path(cookiePathOption)
                .secure(secureOption)
                .httpOnly(false)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "value")
                .maxAge(0)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}
