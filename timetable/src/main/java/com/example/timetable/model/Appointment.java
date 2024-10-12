package com.example.timetable.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime time;
    private Long user_id;
    private Long timetableId;
    public Appointment () {}

    public Appointment(LocalDateTime time, Long user_id, Long timetableId) {
        this.time = time;
        this.user_id = user_id;
        this.timetableId = timetableId;
    }
}

