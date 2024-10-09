package com.example.timetable.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalResponse {
    private Long id;
    private String name;
    private String address;
    private String contactPhone;
    private boolean isActive = false;
}
