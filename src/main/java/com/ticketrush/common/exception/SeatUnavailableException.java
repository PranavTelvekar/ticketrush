package com.ticketrush.common.exception;

import java.util.List;

/**
 * Thrown when a user tries to hold seats that someone else already holds or has booked.
 */
public class SeatUnavailableException extends BusinessRuleException {

    private final List<Long> seatIds;

    public SeatUnavailableException(List<Long> seatIds) {
        super("Some selected seats are no longer available: " + seatIds);
        this.seatIds = List.copyOf(seatIds);
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }
}
