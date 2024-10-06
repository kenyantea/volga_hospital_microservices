package com.example.demoauth.pojo.response;

import com.example.demoauth.models.User;
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
}

