package com.example.domain;

import com.example.domain.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "timeslot")
public class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date start;
    private Date end;
    private boolean isReserved;
}
