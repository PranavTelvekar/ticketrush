package com.ticketrush.venue;

import com.ticketrush.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Seat> seats = new ArrayList<>();

    public Venue(String name, String city, String address) {
        this.name = name;
        this.city = city;
        this.address = address;
    }

    /** Adds a seat to this venue and keeps both sides of the relationship in sync. */
    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setVenue(this);
        totalSeats = seats.size();
    }

    /** Read-only view, so seats can only be added through {@link #addSeat(Seat)}. */
    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }
}
