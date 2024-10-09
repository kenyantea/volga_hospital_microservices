package com.example.timetable.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimetableRequest {
    private Long hospitalId;
    private Long doctorId;
    private String from;
    private String to;
    private String room;
}
