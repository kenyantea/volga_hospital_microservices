package com.example.demoauth.pojo.response;

import lombok.Getter;
import lombok.Setter;

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
