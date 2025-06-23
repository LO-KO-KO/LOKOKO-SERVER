package com.lokoko.global.auth.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokoko.global.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final static String LOG_FORMAT = "ExceptionClass: {}, Message: {}";
    private final static String CONTENT_TYPE = "application/json";
    private final static String CHAR_ENCODING = "UTF-8";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error(LOG_FORMAT, accessDeniedException.getClass().getSimpleName(), accessDeniedException.getMessage(),
                accessDeniedException);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHAR_ENCODING);

        ApiResponse<Void> body = ApiResponse.error(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
        String json = new ObjectMapper().writeValueAsString(body);
        response.getWriter().write(json);
    }
}
