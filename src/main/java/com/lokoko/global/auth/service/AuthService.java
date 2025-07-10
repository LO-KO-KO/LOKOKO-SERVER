package com.lokoko.global.auth.service;

import static com.lokoko.global.auth.jwt.utils.JwtProvider.EMAIL_CLAIM;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.auth.exception.OauthException;
import com.lokoko.global.auth.exception.StateValidationException;
import com.lokoko.global.auth.jwt.dto.LoginDto;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.auth.line.LineOAuthClient;
import com.lokoko.global.auth.line.LineProperties;
import com.lokoko.global.auth.line.dto.LineProfileResponse;
import com.lokoko.global.auth.line.dto.LineTokenResponse;
import com.lokoko.global.auth.line.dto.LineUserInfoResponse;
import com.lokoko.global.utils.LineConstants;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
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
    public LoginDto loginWithLine(String code, String state) {
        try {
            stateService.verify(state);

            LineTokenResponse tokenResp = oAuthClient.issueToken(code);
            DecodedJWT idToken = JWT.decode(tokenResp.id_token());
            String email = idToken.getClaim(EMAIL_CLAIM).asString();

            LineProfileResponse profile = oAuthClient.fetchProfile(tokenResp.access_token());
            String lineUserId = profile.userId();

            LineUserInfoResponse userInfo = oAuthClient.fetchUserInfo(tokenResp.access_token());
            String displayName = userInfo.name();

            Optional<User> userOpt = userRepository.findByLineId(lineUserId);
            User user;
            OauthLoginStatus loginStatus;

            if (userOpt.isPresent()) {
                user = userOpt.get();
                user.updateLastLoginAt();
                if (email != null) {
                    user.updateEmail(email);
                }
                user.updateDisplayName(displayName);
                userRepository.save(user);
                loginStatus = OauthLoginStatus.LOGIN;
            } else {
                user = User.createLineUser(lineUserId, email, displayName);
                user = userRepository.save(user);
                loginStatus = OauthLoginStatus.REGISTER;
            }
            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name(), lineUserId);
            String tokenId = UUID.randomUUID().toString();
            String refreshToken = jwtProvider.generateRefreshToken(
                    user.getId(), user.getRole().name(), tokenId, lineUserId);

            return LoginDto.of(accessToken, refreshToken, loginStatus, user.getId(), tokenId);
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
        String redirectUri = URLEncoder.encode(props.getRedirectUri(), StandardCharsets.UTF_8);

        return LineConstants.AUTHORIZE_PATH +
                LineConstants.PARAM_RESPONSE_TYPE +
                LineConstants.PARAM_CLIENT_ID + props.getClientId() +
                LineConstants.PARAM_REDIRECT_URI + redirectUri +
                LineConstants.PARAM_STATE + state +
                LineConstants.PARAM_SCOPE;
    }
}
