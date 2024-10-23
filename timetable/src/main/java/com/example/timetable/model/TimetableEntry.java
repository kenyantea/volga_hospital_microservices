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
public class TimetableEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long hospitalId;
    private Long doctorId;
    private LocalDateTime start;
    private LocalDateTime end_time;
    private String room;
    private boolean is_active = false;
    public TimetableEntry() {
    }

    public TimetableEntry(Long hospitalId, Long doctorId, LocalDateTime from, LocalDateTime to, String room) {
        this.hospitalId = hospitalId;
        this.doctorId = doctorId;
        this.start = from;
        this.end_time = to;
        this.room = room;
        this.is_active = true;
    }
}
