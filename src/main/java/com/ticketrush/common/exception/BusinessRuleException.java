package com.ticketrush.common.exception;

/**
 * Thrown when a request is valid in shape but breaks a business rule
 * (for example: publishing an event that is already cancelled).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
