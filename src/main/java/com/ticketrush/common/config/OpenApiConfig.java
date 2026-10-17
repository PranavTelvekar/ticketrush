package com.ticketrush.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * The cover page of the API documentation, plus the "Authorize" button for JWT tokens.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TicketRush API",
                version = "v1",
                description = "Book seats for concerts, sports and theatre events without double-booking.",
                contact = @Contact(name = "Pranav Telvekar")),
        security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
@SecurityScheme(
        name = OpenApiConfig.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the accessToken from POST /api/v1/auth/login (without the word Bearer).")
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";
}
