package com.ticketrush.auth.jwt;

import com.ticketrush.user.Role;

/**
 * Who is calling, as read from a valid JWT. No database lookup needed.
 */
public record AuthUser(Long id, String email, Role role) {
}
