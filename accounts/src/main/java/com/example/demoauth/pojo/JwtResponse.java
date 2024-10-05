package com.example.demoauth.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JwtResponse {
	
	private String authToken;
	private String refreshToken;


	public JwtResponse(String authToken, String refreshToken) {

		this.authToken = authToken;
		this.refreshToken = refreshToken;
	}

}
