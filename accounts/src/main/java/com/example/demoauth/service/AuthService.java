package com.example.demoauth.service;

import com.example.demoauth.configs.jwt.JwtUtils;
import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import com.example.demoauth.models.User;
import com.example.demoauth.pojo.request.LoginRequest;
import com.example.demoauth.pojo.request.RefreshRequest;
import com.example.demoauth.pojo.request.SignupRequest;
import com.example.demoauth.pojo.response.JwtResponse;
import com.example.demoauth.pojo.response.MessageResponse;
import com.example.demoauth.repository.RoleRepository;
import com.example.demoauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRespository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public ResponseEntity<?> createUser(SignupRequest signupRequest) {
        if (userRespository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username already exists!"));
        }

        if (signupRequest.getLastName() == null || signupRequest.getFirstName() == null
                || signupRequest.getUsername() == null || signupRequest.getPassword() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Please check you filled all the fields: lastName, firstName, username, and password."));
        }

        User user = new User(signupRequest.getLastName(),
                signupRequest.getFirstName(),
                signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository
                .findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role USER is not found. Please add this role to the database"));
        roles.add(userRole);

        user.setRoles(roles);
        userRespository.save(user);
        return ResponseEntity.ok(new MessageResponse("User CREATED"));
    }

    public ResponseEntity<?> signinUser (LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElse(null);
        if (currentUser == null || !currentUser.isActive()) {
            return ResponseEntity.notFound().build();
        }

        String jwt = jwtUtils.generateJwtToken(authentication);
        String refresh = jwtUtils.generateRefreshJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, refresh));
    }

    public ResponseEntity<?> signoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return ResponseEntity.ok("Successfully logged out.");
    }


    public ResponseEntity<?> validateToken(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            System.out.println(jwtUtils.getRoleFromJwtToken(token));
            return ResponseEntity.ok(jwtUtils.getRoleFromJwtToken(token));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> refreshToken(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        if (jwtUtils.validateRefreshToken(refreshToken)) {
            String username = jwtUtils.getUserNameFromRefreshToken(refreshToken);
            String newAccessToken = jwtUtils.generateJwtToken(username);
            String newRefreshToken = jwtUtils.generateRefreshJwtToken(username);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid refresh token!"));
        }
    }

}
