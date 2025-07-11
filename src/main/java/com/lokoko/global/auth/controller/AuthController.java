package com.lokoko.global.auth.controller;

import static com.lokoko.global.auth.controller.enums.ResponseMessage.LOGIN_SUCCESS;
import static com.lokoko.global.auth.controller.enums.ResponseMessage.REFRESH_TOKEN_REISSUE;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.ACCESS_TOKEN_HEADER;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.REFRESH_TOKEN_HEADER;

import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.jwt.dto.LoginDto;
import com.lokoko.global.auth.jwt.service.JwtService;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.line.dto.LineLoginResponse;
import com.lokoko.global.auth.service.AuthService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AUTH")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "라인 소셜 로그인, 리다이렉션")
    @GetMapping("/line/redirect")
    public void redirectToLineAuth(HttpServletResponse response) throws IOException {
        String authorizeUrl = authService.generateLineLoginUrl();
        response.sendRedirect(authorizeUrl);
    }

    @Operation(summary = "라인 소셜 로그인, JWT 토큰 발급 후 저장")
    @GetMapping("/line/login")
    public ApiResponse<LineLoginResponse> lineLogin(@RequestParam("code") String code,
                                                    @RequestParam("state") String state, HttpServletResponse response) {
        LoginDto tokens = authService.loginWithLine(code, state);
        LineLoginResponse loginResponse = LineLoginResponse.from(tokens);
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, tokens.accessToken(), response);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, tokens.refreshToken(), response);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), loginResponse);
    }

    /**
     * Todo: 테스트용으로 JWT 토큰을 발급하고, 쿠키와 헤더에 저장하는 엔드포인트, 추후 제거 예정
     */
    @Operation(summary = "테스트용 JWT 토큰 발급")
    @PostMapping("/login")
    public ApiResponse<JwtLoginResponse> login(@RequestBody @Valid TestLoginRequest request) {
        JwtTokenDto tokenDto = jwtService.issueTokensForTest(request.userId());
        JwtLoginResponse loginResponse = JwtLoginResponse.of(tokenDto);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), loginResponse);
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
