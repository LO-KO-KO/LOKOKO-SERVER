package com.lokoko.global.auth.service;

import static com.lokoko.global.utils.LineConstants.AUTHORIZE_PATH;
import static com.lokoko.global.utils.LineConstants.PARAM_CLIENT_ID;
import static com.lokoko.global.utils.LineConstants.PARAM_REDIRECT_URI;
import static com.lokoko.global.utils.LineConstants.PARAM_RESPONSE_TYPE;
import static com.lokoko.global.utils.LineConstants.PARAM_SCOPE;
import static com.lokoko.global.utils.LineConstants.PARAM_STATE;

import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.auth.exception.OauthException;
import com.lokoko.global.auth.exception.StateValidationException;
import com.lokoko.global.auth.jwt.dto.JwtTokenDto;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.auth.line.LineOAuthClient;
import com.lokoko.global.auth.line.LineProperties;
import com.lokoko.global.auth.line.dto.LineProfileResponse;
import com.lokoko.global.auth.line.dto.LineTokenResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final StateService stateService;
    private final LineOAuthClient oAuthClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final LineProperties props;

    @Transactional
    public JwtTokenDto loginWithLine(String code) {
        try {
            LineTokenResponse tokenResp = oAuthClient.issueToken(code);

            LineProfileResponse profile = oAuthClient.fetchProfile(tokenResp.access_token());
            String lineUserId = profile.userId();

            Optional<User> userOpt = userRepository.findByLineId(lineUserId);
            User user;
            OauthLoginStatus loginStatus;

            if (userOpt.isPresent()) {
                user = userOpt.get();
                user.updateLastLoginAt();
                userRepository.save(user);
                loginStatus = OauthLoginStatus.LOGIN;
            } else {
                user = User.createLineUser(lineUserId);
                user = userRepository.save(user);
                loginStatus = OauthLoginStatus.REGISTER;
            }

            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
            String refreshToken = jwtProvider.generateRefreshToken(user.getId());

            return JwtTokenDto.of(accessToken, refreshToken, loginStatus);

        } catch (StateValidationException ex) {
            log.warn("State 검증 실패: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("LINE OAuth 로그인 처리 중 오류 발생", ex);
            throw new OauthException(ErrorMessage.OAUTH_ERROR);
        }
    }

    public String generateLineLoginUrl() {
        String state = stateService.generateState();

        return AUTHORIZE_PATH +
                PARAM_RESPONSE_TYPE +
                PARAM_CLIENT_ID + props.getClientId() +
                PARAM_REDIRECT_URI + props.getRedirectUri() +
                PARAM_STATE + state +
                PARAM_SCOPE;
    }
}
