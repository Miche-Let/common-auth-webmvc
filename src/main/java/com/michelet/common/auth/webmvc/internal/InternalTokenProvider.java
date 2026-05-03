package com.michelet.common.auth.webmvc.internal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

public class InternalTokenProvider {
    private final SecretKey secretKey;

    public InternalTokenProvider(String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public InternalTokenClaims parse(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new InternalTokenClaims(
                claims.getIssuer(),
                claims.getAudience()
        );
    }
}
