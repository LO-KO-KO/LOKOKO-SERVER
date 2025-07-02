package com.lokoko.global.auth.line;

import com.lokoko.global.auth.line.dto.LineProfileResponse;
import com.lokoko.global.auth.line.dto.LineTokenResponse;
import com.lokoko.global.auth.line.dto.LineUserInfoResponse;
import com.lokoko.global.utils.LineConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class LineOAuthClient {
    private final WebClient lineWebClient;
    private final LineProperties props;

    public LineTokenResponse issueToken(String code) {
        return lineWebClient.post()
                .uri(LineConstants.TOKEN_PATH)
                .body(BodyInserters.fromFormData(LineConstants.PARAM_GRANT_TYPE, LineConstants.GRANT_TYPE_AUTH_CODE)
                        .with(LineConstants.PARAM_CODE, code)
                        .with(LineConstants.REDIRECT_URI, props.getRedirectUri())
                        .with(LineConstants.CLIENT_ID, props.getClientId())
                        .with(LineConstants.PARAM_CLIENT_SECRET, props.getClientSecret()))
                .retrieve()
                .bodyToMono(LineTokenResponse.class)
                .block();
    }

    public LineProfileResponse fetchProfile(String accessToken) {
        return lineWebClient.get()
                .uri(LineConstants.PROFILE_PATH)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(LineProfileResponse.class)
                .block();
    }

    public LineUserInfoResponse fetchUserInfo(String accessToken) {
        return lineWebClient
                .get()
                .uri(LineConstants.USER_INFO_PATH)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(LineUserInfoResponse.class)
                .block();
    }
}
