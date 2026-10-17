package com.ticketrush.auth.dto;

import com.ticketrush.user.Role;
import com.ticketrush.user.User;

/**
 * What the API shows about a user. Never contains the password hash.
 */
public record UserResponse(Long id, String name, String email, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
