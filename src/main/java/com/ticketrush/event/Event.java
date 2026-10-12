package com.ticketrush.event;

import com.ticketrush.common.entity.BaseEntity;
import com.ticketrush.common.exception.BusinessRuleException;
import com.ticketrush.venue.Venue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventCategory category;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Event(Venue venue, String title, String description, EventCategory category,
                 Instant startTime, Instant endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BusinessRuleException("Event end time must be after its start time");
        }
        this.venue = venue;
        this.title = title;
        this.description = description;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = EventStatus.DRAFT;
    }

    /** DRAFT -> PUBLISHED. Users can see and book the event after this. */
    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT events can be published (current status: " + status + ")");
        }
        if (!startTime.isAfter(Instant.now())) {
            throw new BusinessRuleException("Cannot publish an event that has already started");
        }
        status = EventStatus.PUBLISHED;
    }

    /** DRAFT or PUBLISHED -> CANCELLED. */
    public void cancel() {
        if (status == EventStatus.CANCELLED) {
            throw new BusinessRuleException("Event is already cancelled");
        }
        status = EventStatus.CANCELLED;
    }

    /** Can users hold seats for this event right now? */
    public boolean isBookable() {
        return status == EventStatus.PUBLISHED && startTime.isAfter(Instant.now());
    }
}
