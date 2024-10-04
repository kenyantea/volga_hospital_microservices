package com.example.demoauth.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponse {
	
	private String message;

	public MessageResponse(String message) {
		this.message = message;
	}

}
