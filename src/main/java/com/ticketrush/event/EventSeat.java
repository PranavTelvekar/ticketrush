package com.ticketrush.event;

import com.ticketrush.common.entity.BaseEntity;
import com.ticketrush.common.exception.BusinessRuleException;
import com.ticketrush.common.exception.SeatUnavailableException;
import com.ticketrush.venue.Seat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

import java.math.BigDecimal;

/**
 * One physical seat for one specific event: it has its own price, status and version.
 * This is the row that users compete for, so it is protected by optimistic locking.
 */
@Entity
@Table(name = "event_seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSeat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventSeatStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    public EventSeat(Event event, Seat seat, BigDecimal price) {
        this.event = event;
        this.seat = seat;
        this.price = price;
        this.status = EventSeatStatus.AVAILABLE;
    }

    /** AVAILABLE -> HELD (a user is paying for it). */
    public void hold() {
        if (status != EventSeatStatus.AVAILABLE) {
            throw new SeatUnavailableException(List.of(getId()));
        }
        status = EventSeatStatus.HELD;
    }

    /** HELD -> BOOKED (payment succeeded). */
    public void book() {
        if (status != EventSeatStatus.HELD) {
            throw new BusinessRuleException("Event seat " + getId() + " must be HELD before it can be booked (status: " + status + ")");
        }
        status = EventSeatStatus.BOOKED;
    }

    /** HELD or BOOKED -> AVAILABLE (hold expired, or booking cancelled). */
    public void release() {
        if (status == EventSeatStatus.AVAILABLE) {
            throw new BusinessRuleException("Event seat " + getId() + " is already available");
        }
        status = EventSeatStatus.AVAILABLE;
    }
}
