package com.ticketrush.venue;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    /** Seats of one venue, in seat-map order (A1, A2, ..., B1, ...). The venue is NOT loaded. */
    List<Seat> findByVenueIdOrderByRowLabelAscSeatNumberAsc(Long venueId);

    /** Same seats, but the venue is loaded in the same query (JOIN), so seat.getVenue() is usable. */
    @EntityGraph(attributePaths = "venue")
    List<Seat> findWithVenueByVenueIdOrderByRowLabelAscSeatNumberAsc(Long venueId);
}
