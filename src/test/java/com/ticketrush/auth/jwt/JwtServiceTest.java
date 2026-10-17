package com.ticketrush.auth.jwt;

import com.ticketrush.user.Role;
import com.ticketrush.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // Base64 of "unit-test-secret-key-0123456789!" (32 bytes). Test-only, never used by the real app.
    private static final String SECRET = "dW5pdC10ZXN0LXNlY3JldC1rZXktMDEyMzQ1Njc4OSE=";
    // Base64 of "another-secret-key-for-tests-987" (32 bytes)
    private static final String OTHER_SECRET = "YW5vdGhlci1zZWNyZXQta2V5LWZvci10ZXN0cy05ODc=";

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, Duration.ofHours(1)));

    @Test
    void tokenContainsWhoTheUserIs() {
        String token = jwtService.generateToken(user(7L, "pranav@example.com", Role.ADMIN));

        AuthUser authUser = jwtService.parseToken(token);

        assertThat(authUser.id()).isEqualTo(7L);
        assertThat(authUser.email()).isEqualTo("pranav@example.com");
        assertThat(authUser.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void tokenHasThreeParts() {
        String token = jwtService.generateToken(user(7L, "pranav@example.com", Role.USER));

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void changedTokenIsRejected() {
        String token = jwtService.generateToken(user(7L, "pranav@example.com", Role.USER));
        String[] parts = token.split("\\.");
        String forgedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"7\",\"email\":\"pranav@example.com\",\"role\":\"ADMIN\"}".getBytes());
        String forgedToken = parts[0] + "." + forgedPayload + "." + parts[2];

        assertThatThrownBy(() -> jwtService.parseToken(forgedToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void tokenFromAnotherKeyIsRejected() {
        JwtService otherService = new JwtService(new JwtProperties(OTHER_SECRET, Duration.ofHours(1)));
        String token = otherService.generateToken(user(7L, "pranav@example.com", Role.USER));

        assertThatThrownBy(() -> jwtService.parseToken(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService shortLived = new JwtService(new JwtProperties(SECRET, Duration.ofSeconds(-1)));
        String token = shortLived.generateToken(user(7L, "pranav@example.com", Role.USER));

        assertThatThrownBy(() -> jwtService.parseToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void garbageIsRejected() {
        assertThatThrownBy(() -> jwtService.parseToken("not-a-jwt"))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void tooShortSecretIsRefusedAtStartup() {
        // Base64 of "too-short-key" (13 bytes), far below the 32 bytes HS256 needs
        assertThatThrownBy(() -> new JwtService(new JwtProperties("dG9vLXNob3J0LWtleQ==", Duration.ofHours(1))))
                .isInstanceOf(WeakKeyException.class);
    }

    private static User user(Long id, String email, Role role) {
        User user = new User("Test User", email, "not-a-real-hash", role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
