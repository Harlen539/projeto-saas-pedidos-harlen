package com.harlen.saas_pedidos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;
    private final long passwordResetExpirationMs;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-ms}") long expirationMs,
        @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs,
        @Value("${app.password-reset.expiration-ms}") long passwordResetExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        this.passwordResetExpirationMs = passwordResetExpirationMs;
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();

        return Jwts.builder()
            .subject(user.getUsername())
            .claim("userId", user.getId())
            .claim("empresaId", user.getEmpresaId())
            .claim("nome", user.getNome())
            .claim("role", user.getRole().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(secretKey)
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, AuthenticatedUser user) {
        Claims claims = extractAllClaims(token);
        Long empresaId = claims.get("empresaId", Long.class);
        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);
        Date expiration = claims.getExpiration();

        return user.getUsername().equalsIgnoreCase(claims.getSubject())
            && user.getId().equals(userId)
            && user.getEmpresaId().equals(empresaId)
            && user.getRole().name().equals(role)
            && expiration.after(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public long getPasswordResetExpirationMs() {
        return passwordResetExpirationMs;
    }
}
