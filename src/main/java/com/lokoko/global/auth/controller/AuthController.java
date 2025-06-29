package com.lokoko.global.auth.controller;

import static com.lokoko.global.auth.controller.enums.ResponseMessage.LOGIN_SUCCESS;
import static com.lokoko.global.auth.controller.enums.ResponseMessage.URL_GET_SUCCESS;

import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.line.dto.LineLoginResponse;
import com.lokoko.global.auth.line.dto.LoginUrlResponse;
import com.lokoko.global.auth.service.AuthService;
import com.lokoko.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/line/login")
    public ApiResponse<LineLoginResponse> lineLogin(@RequestParam("code") String code) {
        JwtTokenDto tokens = authService.loginWithLine(code);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), new LineLoginResponse(tokens));
    }

    @GetMapping("/url")
    public ApiResponse<LoginUrlResponse> getLoginUrl() {
        String url = authService.generateLineLoginUrl();

        return ApiResponse.success(HttpStatus.OK, URL_GET_SUCCESS.getMessage(), new LoginUrlResponse(url));
    }
}
