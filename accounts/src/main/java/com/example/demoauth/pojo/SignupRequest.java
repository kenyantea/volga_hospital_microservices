package com.example.demoauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SignupRequest {
	@JsonProperty(required = true)
	private String lastName;

	@JsonProperty(required = true)
	private String firstName;

	@JsonProperty(required = true)
	private String username;

	@JsonProperty(required = true)
	private String password;
}
