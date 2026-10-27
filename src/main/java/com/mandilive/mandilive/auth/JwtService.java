package com.mandilive.mandilive.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mandilive.mandilive.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl}") Duration accessTtl,
            @Value("${jwt.refresh-ttl}") Duration refreshTtl) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String generateAccessToken(User user) {
        return build(user, TYPE_ACCESS, accessTtl);
    }

    public String generateRefreshToken(User user) {
        return build(user, TYPE_REFRESH, refreshTtl);
    }

    public long accessTtlSeconds() {
        return accessTtl.toSeconds();
    }

    /** Verifies signature and expiry; throws JwtException if invalid. */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    /** Returns the subject (email) if the token is valid and of the expected type. */
    public String extractSubject(String token, String expectedType) {
        Claims claims = parse(token);
        if (!expectedType.equals(claims.get("type", String.class))) {
            throw new JwtException("Wrong token type");
        }
        return claims.getSubject();
    }

    private String build(User user, String type, Duration ttl) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl.toMillis()))
                .signWith(key)
                .compact();
    }
}
