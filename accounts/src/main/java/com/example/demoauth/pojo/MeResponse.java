package com.example.demoauth.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeResponse {
    private String lastName;
    private String firstName;
    private String username;
    private boolean isActive;
}
