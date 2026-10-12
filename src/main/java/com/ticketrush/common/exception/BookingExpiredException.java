package com.ticketrush.common.exception;

/**
 * Thrown when a user tries to pay for a hold whose time is over.
 */
public class BookingExpiredException extends BusinessRuleException {

    public BookingExpiredException(Long bookingId) {
        super("Booking " + bookingId + " hold has expired");
    }
}
