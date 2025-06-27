package com.lokoko.global.auth.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokoko.global.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String LOG_FORMAT = "ExceptionClass: {}, Message: {}, ErrorObj: {}";
    private static final String JWT_ERROR_ATTR = "jwtError";
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHAR_ENCODING = "UTF-8";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        Object jwtErrorObj = request.getAttribute(JWT_ERROR_ATTR);

        String errorMessage;
        String exceptionClass;
        Object logTarget;

        if (jwtErrorObj instanceof com.lokoko.global.auth.exception.ErrorMessage em) {
            errorMessage = em.getMessage();
            exceptionClass = em.getClass().getSimpleName();
            logTarget = em;
        } else if (jwtErrorObj instanceof com.lokoko.global.auth.jwt.exception.JwtErrorMessage jem) {
            errorMessage = jem.getMessage();
            exceptionClass = jem.getClass().getSimpleName();
            logTarget = jem;
        } else {
            errorMessage = authException.getMessage();
            exceptionClass = authException.getClass().getSimpleName();
            logTarget = authException;
        }

        log.error(LOG_FORMAT, exceptionClass, errorMessage, logTarget);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHAR_ENCODING);

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.UNAUTHORIZED,
                errorMessage
        );
        String json = new ObjectMapper().writeValueAsString(body);
        response.getWriter().write(json);
    }
}
