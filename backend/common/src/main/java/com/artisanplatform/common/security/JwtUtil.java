package com.artisanplatform.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Issues and validates JWT bearer tokens for the whole platform.
 *
 * <p>Mechanism chosen per docs/architecture/08_SECURITY_AND_VAULT.md Part
 * C.1 — Spring Security is explicit in the source workflow, but the exact
 * token scheme is not, so a stateless JWT bearer token was the reasoned
 * default for a mobile REST client. Token lifetimes are configurable via
 * {@code JWT_ACCESS_TOKEN_TTL_MINUTES} / {@code JWT_REFRESH_TOKEN_TTL_DAYS}
 * (see .env.example).
 */
@Component
public class JwtUtil {

    private final Key signingKey;
    private final long accessTokenTtlMinutes;
    private final long refreshTokenTtlDays;

    public JwtUtil(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-token-ttl-minutes:30}") long accessTokenTtlMinutes,
            @Value("${app.security.jwt.refresh-token-ttl-days:14}") long refreshTokenTtlDays) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public String generateAccessToken(UUID userId, String email, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtlMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshTokenTtlDays, ChronoUnit.DAYS)))
                .signWith(signingKey)
                .compact();
    }

    /** @throws io.jsonwebtoken.JwtException if the token is expired, malformed, or has an invalid signature. */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) parseClaims(token).get("roles");
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parseClaims(token).get("type"));
    }
}
