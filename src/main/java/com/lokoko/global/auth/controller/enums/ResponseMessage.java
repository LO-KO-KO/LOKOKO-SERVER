package com.lokoko.global.auth.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    LOGIN_SUCCESS("회원가입/로그인 성공에 성공했습니다."),
    URL_GET_SUCCESS("리다이렉트 URL 조회에 성공했습니다."),
    REFRESH_TOKEN_REISSUE("리프레시 토큰 재발급에 성공했습니다.");

    private final String message;
}
