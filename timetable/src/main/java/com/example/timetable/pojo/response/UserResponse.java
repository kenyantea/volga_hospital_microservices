package com.example.timetable.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String lastName;
    private String firstName;
    private String username;
    private boolean active;
}
