package com.lokoko.global.auth.controller;

import static com.lokoko.global.auth.controller.enums.ResponseMessage.LOGIN_SUCCESS;
import static com.lokoko.global.auth.controller.enums.ResponseMessage.REFRESH_TOKEN_REISSUE;
import static com.lokoko.global.auth.controller.enums.ResponseMessage.URL_GET_SUCCESS;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.AUTHORIZATION_HEADER;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.REFRESH_TOKEN_HEADER;

import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.jwt.service.JwtService;
import com.lokoko.global.auth.line.dto.LineLoginResponse;
import com.lokoko.global.auth.line.dto.LoginUrlResponse;
import com.lokoko.global.auth.service.AuthService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(summary = "라인 소셜 로그인, JWT 토큰 발급")
    @GetMapping("/line/login")
    public ApiResponse<LineLoginResponse> lineLogin(@RequestParam("code") String code) {
        JwtTokenDto tokens = authService.loginWithLine(code);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), new LineLoginResponse(tokens));
    }

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
        responseToken(jwtTokenDto, response);

        return ApiResponse.success(HttpStatus.OK, REFRESH_TOKEN_REISSUE.getMessage());
    }

    private void responseToken(JwtTokenDto jwtTokenDto, HttpServletResponse response) {
        response.setHeader(AUTHORIZATION_HEADER, jwtTokenDto.accessToken());
        response.setHeader(REFRESH_TOKEN_HEADER, jwtTokenDto.refreshToken());
    }
}
