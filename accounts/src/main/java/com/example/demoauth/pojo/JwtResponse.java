package com.example.demoauth.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JwtResponse {
	
	private String token;


	public JwtResponse(String token) {
		this.token = token;
	}

}
