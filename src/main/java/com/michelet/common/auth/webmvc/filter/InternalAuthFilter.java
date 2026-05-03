package com.michelet.common.auth.webmvc.filter;

import com.michelet.common.auth.webmvc.config.InternalAuthProperties;
import com.michelet.common.auth.webmvc.internal.InternalTokenClaims;
import com.michelet.common.auth.webmvc.internal.InternalTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class InternalAuthFilter extends OncePerRequestFilter {

    private static final String INTERNAL_HEADER = "X-Internal-Token";

    private final InternalTokenProvider internalTokenProvider;
    private final InternalAuthProperties internalAuthProperties;

    public InternalAuthFilter(
            InternalTokenProvider internalTokenProvider,
            InternalAuthProperties internalAuthProperties
    ) {
        this.internalTokenProvider = internalTokenProvider;
        this.internalAuthProperties = internalAuthProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader(INTERNAL_HEADER);
        if (token == null || token.isBlank()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "내부 토큰이 필요합니다.");
            return;
        }
        try {
            InternalTokenClaims claims = internalTokenProvider.parse(token);
            if (claims.issuer() == null || claims.issuer().isBlank()) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 내부 토큰입니다.");
                return;
            }
            if (claims.audience() == null
                    || !claims.audience().contains(internalAuthProperties.getAudience())) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "유효하지 않은 대상 서비스입니다.");
                return;
            }
            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 내부 토큰입니다.");
        } catch (RuntimeException e) {
                       throw e;
        }
    }
}
