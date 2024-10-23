package com.example.demoauth.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String lastName;
    private String firstName;
    private String password;
}
