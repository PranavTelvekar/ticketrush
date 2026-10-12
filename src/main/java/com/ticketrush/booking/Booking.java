package com.ticketrush.booking;

import com.ticketrush.common.entity.BaseEntity;
import com.ticketrush.common.exception.BusinessRuleException;
import com.ticketrush.common.exception.BookingExpiredException;
import com.ticketrush.event.Event;
import com.ticketrush.event.EventSeat;
import com.ticketrush.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An order made by one user for one or more seats of one event.
 * The booking drives the status of its seats: hold, book and release always happen through it.
 */
@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToMany
    @JoinTable(
            name = "booking_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "event_seat_id"))
    @Getter(AccessLevel.NONE)
    private Set<EventSeat> seats = new HashSet<>();

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Holds the given seats for this user. The hold ends after {@code holdDuration} unless it is paid. */
    public Booking(User user, Event event, List<EventSeat> seatsToHold, Duration holdDuration) {
        if (seatsToHold.isEmpty()) {
            throw new BusinessRuleException("A booking needs at least one seat");
        }
        if (!event.isBookable()) {
            throw new BusinessRuleException("Event " + event.getId() + " is not open for booking");
        }
        for (EventSeat seat : seatsToHold) {
            if (!seat.getEvent().getId().equals(event.getId())) {
                throw new BusinessRuleException("Event seat " + seat.getId() + " does not belong to event " + event.getId());
            }
            seat.hold();
        }
        this.user = user;
        this.event = event;
        this.seats.addAll(seatsToHold);
        this.totalAmount = seatsToHold.stream()
                .map(EventSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.status = BookingStatus.PENDING;
        this.expiresAt = Instant.now().plus(holdDuration);
    }

    /** PENDING -> CONFIRMED (payment succeeded). Its seats become BOOKED. */
    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING bookings can be confirmed (current status: " + status + ")");
        }
        if (isHoldExpired()) {
            throw new BookingExpiredException(getId());
        }
        seats.forEach(EventSeat::book);
        status = BookingStatus.CONFIRMED;
    }

    /** PENDING -> EXPIRED (nobody paid in time). Its seats become AVAILABLE again. */
    public void expire() {
        if (status != BookingStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING bookings can expire (current status: " + status + ")");
        }
        if (!isHoldExpired()) {
            throw new BusinessRuleException("Booking hold has not expired yet");
        }
        seats.forEach(EventSeat::release);
        status = BookingStatus.EXPIRED;
    }

    /** PENDING or CONFIRMED -> CANCELLED. A confirmed booking can only be cancelled before the event starts. */
    public void cancel() {
        if (status != BookingStatus.PENDING && status != BookingStatus.CONFIRMED) {
            throw new BusinessRuleException("Booking cannot be cancelled (current status: " + status + ")");
        }
        if (status == BookingStatus.CONFIRMED && !event.getStartTime().isAfter(Instant.now())) {
            throw new BusinessRuleException("Cannot cancel a booking after the event has started");
        }
        seats.forEach(EventSeat::release);
        status = BookingStatus.CANCELLED;
    }

    /** Is this a hold whose time is over? */
    public boolean isHoldExpired() {
        return status == BookingStatus.PENDING && !expiresAt.isAfter(Instant.now());
    }

    /** Does this booking belong to the given user? */
    public boolean isOwnedBy(Long userId) {
        return user.getId().equals(userId);
    }

    /** Read-only view, so seats can only change through the business methods. */
    public Set<EventSeat> getSeats() {
        return Collections.unmodifiableSet(seats);
    }
}
