package com.example.demoauth.pojo.request;

import com.example.demoauth.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRequest {
    private String lastName;
    private String firstName;
    private String username;
    private String password;
    private Set<String> roles;
}
