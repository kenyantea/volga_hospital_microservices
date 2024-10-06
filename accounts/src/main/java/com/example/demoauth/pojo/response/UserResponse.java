package com.example.demoauth.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String lastName;
    private String firstName;
    private String username;
    private boolean isActive;
}
