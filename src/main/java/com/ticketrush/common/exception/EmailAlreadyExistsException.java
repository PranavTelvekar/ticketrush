package com.ticketrush.common.exception;

/**
 * Thrown when someone tries to register with an email that already has an account.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " is already registered");
    }
}
