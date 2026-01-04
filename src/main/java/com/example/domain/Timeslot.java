package com.example.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "timeslot")
public class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date start;
    private Date end;
    private boolean isReserved;

    public Timeslot() {}

    public Timeslot(Date start, Date end) {
        this.start = start;
        this.end = end;
        this.isReserved = false;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getStart() { return start; }
    public void setStart(Date start) { this.start = start; }

    public Date getEnd() { return end; }
    public void setEnd(Date end) { this.end = end; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { isReserved = reserved; }
}
