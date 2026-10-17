package com.ticketrush.auth.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * The JWT settings from application.yaml ({@code app.jwt.*}).
 *
 * @param secret     Base64-encoded HMAC key, at least 32 bytes (256 bits) after decoding
 * @param expiration how long a token stays valid, for example {@code 1h}
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(@NotBlank String secret, @NotNull Duration expiration) {
}