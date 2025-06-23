package com.lokoko.global.auth.jwt.filter;

import static com.lokoko.global.auth.jwt.exception.ErrorMessage.JWT_TOKEN_EXPIRED;
import static com.lokoko.global.auth.jwt.exception.ErrorMessage.JWT_TOKEN_INVALID;
import static com.lokoko.global.auth.jwt.exception.ErrorMessage.JWT_TOKEN_NOT_FOUND;

import com.lokoko.global.auth.jwt.principal.JwtUserDetails;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final static String JWT_ERROR = "jwtError";
    private final JwtExtractor jwtExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<String> token = jwtExtractor.extractJwtToken(request);

        if (token.isEmpty()) {
            request.setAttribute(JWT_ERROR, JWT_TOKEN_NOT_FOUND);
            filterChain.doFilter(request, response);

            return;
        }
        String accessToken = token.get();

        if (!jwtExtractor.validateJwtToken(accessToken)) {
            request.setAttribute(JWT_ERROR, JWT_TOKEN_INVALID);
            filterChain.doFilter(request, response);

            return;
        }

        if (jwtExtractor.isExpired(accessToken)) {
            request.setAttribute(JWT_ERROR, JWT_TOKEN_EXPIRED);
            filterChain.doFilter(request, response);

            return;
        }
        saveAuthentcation(accessToken);
        filterChain.doFilter(request, response);
    }

    private void saveAuthentcation(String token) {
        Long id = jwtExtractor.getId(token);
        String email = jwtExtractor.getEmail(token);
        String role = jwtExtractor.getRole(token);

        UserDetails userDetails = JwtUserDetails.of(id, email, role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
