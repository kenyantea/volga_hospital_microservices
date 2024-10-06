package com.example.demoauth.controllers;

import com.example.demoauth.pojo.request.LoginRequest;
import com.example.demoauth.pojo.request.RefreshRequest;
import com.example.demoauth.pojo.request.SignupRequest;
import com.example.demoauth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
	@Autowired
	AuthService userService;
	
	@PostMapping("/SignIn")
	public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
		return userService.signinUser(loginRequest);
	}
	
	@PostMapping("/SignUp")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
		return userService.createUser(signupRequest);
	}

	@PutMapping("/SignOut")
	@PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('DOCTOR')")
	public ResponseEntity<?> signoutUser() {
		return userService.signoutUser();
	}

	@GetMapping("/Validate")
	public ResponseEntity<?> validateToken(@RequestParam String accessToken) {
		return userService.validateToken(accessToken);
	}

	@PostMapping("/Refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
		return userService.refreshToken(refreshRequest);
	}
}
