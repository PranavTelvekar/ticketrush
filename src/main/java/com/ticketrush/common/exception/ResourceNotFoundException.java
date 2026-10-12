package com.ticketrush.common.exception;

/**
 * Thrown when something the client asked for by id does not exist (for example: event 99).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " " + id + " not found");
    }
}
