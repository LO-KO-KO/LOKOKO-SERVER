package com.lokoko.global.auth.controller;

import static com.lokoko.global.auth.controller.enums.ResponseMessage.REFRESH_TOKEN_REISSUE;
import static com.lokoko.global.auth.controller.enums.ResponseMessage.URL_GET_SUCCESS;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.ACCESS_TOKEN_HEADER;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.REFRESH_TOKEN_HEADER;

import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.jwt.dto.LoginDto;
import com.lokoko.global.auth.jwt.service.JwtService;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.line.dto.LoginUrlResponse;
import com.lokoko.global.auth.service.AuthService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AUTH")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "라인 소셜 로그인, 리다이렉션 (백엔드 주소로)")
    @GetMapping("/line/redirect")
    public void redirectToLineAuth(HttpServletResponse response) throws IOException {
        String authorizeUrl = authService.generateLineLoginUrl();
        response.sendRedirect(authorizeUrl);
    }

    @Operation(summary = "라인 소셜 로그인, JWT 토큰 발급 후 저장")
    @GetMapping("/line/login")
    public LoginDto lineLogin(@RequestParam("code") String code, @RequestParam("state") String state) {
        LoginDto tokens = authService.loginWithLine(code, state);
        return tokens;
    }

    @Operation(summary = "JWT 토큰 쿠키에 저장")
    @GetMapping("/line/token/cookie")
    public void setJwtCookie(@RequestParam("accessToken") String accessToken,
                             @RequestParam("refreshToken") String refreshToken,
                             HttpServletResponse response
    ) {
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, accessToken, response);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, refreshToken, response);
    }
    /*
     * TODO: 명세서 작성 후 플로우 확정 예정
     */

    @Operation(summary = "라인 로그인 URL 생성 (클라에서 호출)")
    @GetMapping("/url")
    public ApiResponse<LoginUrlResponse> getLoginUrl() {
        String url = authService.generateLineLoginUrl();

        return ApiResponse.success(HttpStatus.OK, URL_GET_SUCCESS.getMessage(), new LoginUrlResponse(url));
    }

    @PostMapping("/refresh")
    @Operation(summary = "RefreshToken 재발급")
    public ApiResponse<Void> reissueRefreshToken(@RequestHeader(REFRESH_TOKEN_HEADER) String refreshToken,
                                                 HttpServletResponse response) {
        JwtTokenDto jwtTokenDto = jwtService.reissueJwtToken(refreshToken);
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, jwtTokenDto.accessToken(), response);
        response.setHeader(REFRESH_TOKEN_HEADER, jwtTokenDto.refreshToken());

        return ApiResponse.success(HttpStatus.OK, REFRESH_TOKEN_REISSUE.getMessage());
    }
}
