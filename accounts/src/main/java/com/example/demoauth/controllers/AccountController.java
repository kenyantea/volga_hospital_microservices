package com.example.demoauth.controllers;

import com.example.demoauth.pojo.MeResponse;
import com.example.demoauth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {
    @Autowired
    AuthService authService;

    @GetMapping("/Me")
    //@PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<?> getUserInfo()
    {
        return ResponseEntity.ok().build();
    }
}
