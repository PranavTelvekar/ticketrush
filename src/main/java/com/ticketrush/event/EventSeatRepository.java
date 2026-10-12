package com.ticketrush.event;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    /** The seat map of one event (A1, A2, ..., B1, ...), with the physical seat loaded for labels. */
    @EntityGraph(attributePaths = "seat")
    List<EventSeat> findByEventIdOrderBySeatRowLabelAscSeatSeatNumberAsc(Long eventId);

    /** How many seats of an event have a given status (e.g. how many are still AVAILABLE). */
    long countByEventIdAndStatus(Long eventId, EventSeatStatus status);
}
