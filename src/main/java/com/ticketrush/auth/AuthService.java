package com.ticketrush.auth;

import com.ticketrush.auth.dto.RegisterRequest;
import com.ticketrush.auth.dto.UserResponse;
import com.ticketrush.common.exception.EmailAlreadyExistsException;
import com.ticketrush.user.Role;
import com.ticketrush.user.User;
import com.ticketrush.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Creates accounts (and, from Step 19, logs people in).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = userRepository.save(new User(request.name().trim(), email, passwordHash, Role.USER));

        log.info("New user registered with id {}", user.getId());
        return UserResponse.from(user);
    }
}
