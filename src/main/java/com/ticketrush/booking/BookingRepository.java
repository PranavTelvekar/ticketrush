package com.ticketrush.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /** One booking with its event, its event seats and their physical seats (for labels like "A1"). */
    @EntityGraph(attributePaths = {"event", "seats", "seats.seat"})
    Optional<Booking> findWithSeatsById(Long id);

    /** "My bookings" - newest first. */
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Holds whose time ran out - used by the expiry job (index idx_bookings_status_expires). */
    @EntityGraph(attributePaths = "seats")
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, Instant time);
}
