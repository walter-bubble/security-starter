package com.walter.toolkit.security_starter.security;

import com.walter.toolkit.security_starter.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {
    private static final String AUTHORITIES_CLAIM = "authorities";
    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] decodedSecret = Decoders.BASE64.decode(jwtProperties.secret());
        this.signingKey = Keys.hmacShaKeyFor(decodedSecret);
    }

    public String generateAccessToken(String subject, Collection<String> authorities) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Jwt subject must not be blank");
        }
        Objects.requireNonNull(authorities, "Authorities must not be null");
        List<String>authorityList = authorities.stream()
                .map(authority -> {
                    if (authority == null || authority.isBlank()) {
                        throw new IllegalArgumentException("Authority must not be blank");
                    }
                    return authority;
                })
                .distinct()
                .toList();
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(jwtProperties.accessTokenExpiration());
        return Jwts.builder()
                .subject(subject)
                .issuer(jwtProperties.issuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .claim(AUTHORITIES_CLAIM, authorityList)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> extractAuthorities(String token) {
        Object authorities = parseClaims(token).get(AUTHORITIES_CLAIM);
        if (!(authorities instanceof List<?>authorityList)){
            return List.of();
        }
        return authorityList.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }

    public boolean isTokenValid(String token, String expectedSubject) {
        if (token == null || token.isBlank() || expectedSubject == null || expectedSubject.isBlank()) {
            return false;
        }
        try {
            Claims claims = parseClaims(token);
            boolean isSubjectCorrect = expectedSubject.equals(claims.getSubject());
            Date expiration = claims.getExpiration();
            boolean isNotExpired = expiration != null && expiration.after(new Date());
            return isSubjectCorrect && isNotExpired;
        } catch (Exception exception) {
            return false;
        }
    }
    private Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ERR_INVALID_TOKEN", "JWT token must not be blank");
        }
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(jwtProperties.issuer())
                    .clockSkewSeconds(jwtProperties.clockSkewSeconds())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "ERR_BAD_CREDENTIALS", "The provided authentication token is invalid or expired");
        }
    }
}
