package com.ticketrush.auth.jwt;

import com.ticketrush.user.Role;
import com.ticketrush.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Creates signed login tokens (JWT) and checks tokens that clients send back.
 */
@Service
public class JwtService {

    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    private final SecretKey signingKey;
    private final Duration expiration;
    private final JwtParser parser;

    public JwtService(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
        this.expiration = properties.expiration();
        this.parser = Jwts.parser().verifyWith(signingKey).build();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(ROLE_CLAIM, user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Checks the signature and expiry, then reads who the token belongs to.
     *
     * @throws JwtException if the token is malformed, tampered with, signed with another key or expired
     */
    public AuthUser parseToken(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();
        return new AuthUser(
                Long.valueOf(claims.getSubject()),
                claims.get(EMAIL_CLAIM, String.class),
                Role.valueOf(claims.get(ROLE_CLAIM, String.class)));
    }
}
