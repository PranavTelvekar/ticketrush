package com.ticketrush.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The JSON a guest sends to create an account.
 */
public record RegisterRequest(

        @Schema(example = "Pranav Telvekar")
        @NotBlank
        @Size(min = 2, max = 100)
        String name,

        @Schema(example = "pranav@example.com")
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @Schema(example = "StrongPass@123")
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9]).*$", message = "must contain at least one letter and one digit")
        @Pattern(regexp = "^[ -~]*$", message = "must use only English letters, digits, spaces and keyboard symbols")
        String password) {
}
