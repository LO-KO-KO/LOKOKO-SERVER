package com.lokoko.global.auth.jwt.filter;

import static com.lokoko.global.auth.jwt.exception.JwtErrorMessage.JWT_TOKEN_EXPIRED;
import static com.lokoko.global.auth.jwt.exception.JwtErrorMessage.JWT_TOKEN_INVALID;
import static com.lokoko.global.auth.jwt.exception.JwtErrorMessage.JWT_TOKEN_NOT_FOUND;

import com.lokoko.global.auth.jwt.principal.JwtUserDetails;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "";
    private final static String JWT_ERROR = "jwtError";
    private final static String ROLE = "role";
    private final JwtExtractor jwtExtractor;
    private final JwtProvider jwtProvider;

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
        Jwt springJwt = jwtProvider.toSpringJwt(accessToken);
        Authentication auth = new JwtAuthenticationToken(
                springJwt,
                List.of(new SimpleGrantedAuthority(springJwt.getClaimAsString(ROLE)))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String token) {
        Long id = jwtExtractor.getId(token);
        String lineId = jwtExtractor.getLineId(token);
        String role = jwtExtractor.getRole(token);

        UserDetails userDetails = JwtUserDetails.of(id, lineId, role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
