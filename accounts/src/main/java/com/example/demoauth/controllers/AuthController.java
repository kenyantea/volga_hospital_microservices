package com.example.demoauth.controllers;

import com.example.demoauth.pojo.request.LoginRequest;
import com.example.demoauth.pojo.request.RefreshRequest;
import com.example.demoauth.pojo.request.SignupRequest;
import com.example.demoauth.service.AuthService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(value = "Proceeds authentication", tags = {"Auth Controller"})
@RestController
@RequestMapping("/api/Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
	@Autowired
	AuthService userService;
	
	@PostMapping("/SignIn")
	@ApiOperation(value = "Auth user", notes = "Requires a username and a password and gives a pair of tokens.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success."),
			@ApiResponse(code = 404, message = "User Not Found"),
			@ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description).")
	})
	public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
		return userService.signinUser(loginRequest);
	}

	@ApiOperation(value = "Create new account", notes = "Enter last name, first name, username and password.")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully created."),
			@ApiResponse(code = 400, message = "Bad Request. Perhaps you haven't entered all the parameters or username already exists.")
	})
	@PostMapping("/SignUp")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
		return userService.createUser(signupRequest);
	}

	@ApiOperation(value = "Sign out", notes = "Sign out of an account")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Signed out successfully."),
			@ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
			@ApiResponse(code = 401, message = "You'll have to be logged in first.")
	})
	@PutMapping("/SignOut")
	@PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('DOCTOR')")
	public ResponseEntity<?> signoutUser() {
		return userService.signoutUser();
	}

	@ApiOperation(value = "Validate", notes = "Checks if a token is ok or not")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Token is OK."),
			@ApiResponse(code = 404, message = "Token is not OK"),
			@ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description).")
	})
	@GetMapping("/Validate")
	public ResponseEntity<?> validateToken(@RequestParam String accessToken) {
		return userService.validateToken(accessToken);
	}

	@ApiOperation(value = "Refresh", notes = "Creates new pair of tokens by given refresh token.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success."),
			@ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description).")
	})
	@PostMapping("/Refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
		return userService.refreshToken(refreshRequest);
	}
}
