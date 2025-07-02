package com.lokoko.global.auth.line.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;

public record LineLoginResponse(
        OauthLoginStatus loginStatus
) {
}
