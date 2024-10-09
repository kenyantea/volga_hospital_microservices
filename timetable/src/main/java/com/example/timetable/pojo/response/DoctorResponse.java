package com.example.timetable.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DoctorResponse {
    private String lastName;
    private String firstName;
    private String username;
    private String specialty;
    private boolean active;
}


