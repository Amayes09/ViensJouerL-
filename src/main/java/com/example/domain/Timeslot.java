package com.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * ✅ Timeslot avec relation à Venue pour cohérence du modèle
 * Un créneau horaire appartient toujours à une venue
 */
@Entity
@Table(name = "timeslots")
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date start;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date endTime;

    @Column(nullable = false)
    private boolean isReserved = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Venue venue;

    public Timeslot() {}

    public Timeslot(Date start, Date endTime, Venue venue) {
        this.start = start;
        this.endTime = endTime;
        this.venue = venue;
        this.isReserved = false;
    }

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getStart() { return start; }
    public void setStart(Date start) { this.start = start; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { isReserved = reserved; }

    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }
}
