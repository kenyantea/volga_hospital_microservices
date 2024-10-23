package com.example.demoauth.service;

import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import com.example.demoauth.models.User;
import com.example.demoauth.pojo.request.UpdateUserRequest;
import com.example.demoauth.pojo.request.UserRequest;
import com.example.demoauth.pojo.response.UserResponse;
import com.example.demoauth.repository.RoleRepository;
import com.example.demoauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class AccountService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User updateUser(Authentication authentication, UpdateUserRequest updatedInfo) {
        if (authentication.getName() == null || updatedInfo == null) {
            return null;
        }

        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElse(null);

        if (currentUser == null) {
            return null;
        }

        currentUser.setFirstName(updatedInfo.getFirstName());
        currentUser.setLastName(updatedInfo.getLastName());

        if (updatedInfo.getPassword() != null && !updatedInfo.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(updatedInfo.getPassword());
            currentUser.setPassword(encryptedPassword);
        }

        return userRepository.save(currentUser);
    }

    public ResponseEntity<?> currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElse(null);

        if (currentUser == null || !currentUser.isActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(new UserResponse(currentUser.getId(),
                currentUser.getLastName(),
                currentUser.getFirstName(),
                currentUser.getUsername(),
                true));
    }

    public ResponseEntity<Page<User>> getAccounts(int from, int count) {
        Pageable pageable = PageRequest.of(from / count, count);
        Page<User> accounts = userRepository.findAll(pageable);
        return ResponseEntity.ok(accounts);
    }

    public ResponseEntity<?> createUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }
        try {
            Set<Role> roles = new HashSet<>();

            if (userRequest.getRoles() != null) {
                for (String roleName : userRequest.getRoles()) {
                    Role role = roleRepository.findByName(ERole.valueOf(roleName.toUpperCase())).orElse(null);
                    if (role != null) {
                        roles.add(role);
                    } else {
                        return ResponseEntity.badRequest().body("Role not found. The available roles are: ROLE_USER, ROLE_MANAGER, ROLE_DOCTOR, ROLE_ADMIN");
                    }
                }
            }

            User newUser = new User(userRequest.getLastName(), userRequest.getFirstName(), userRequest.getUsername(),
                    userRequest.getPassword(), roles);

            userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Check the fields and try again.");
        }
    }

    public ResponseEntity<?> updateByAdmin(Long id, UserRequest updatedUser) {
        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            Set<Role> roles = new HashSet<>();
            if (updatedUser.getRoles() != null) {
                for (String roleName : updatedUser.getRoles()) {
                    Role role = roleRepository.findByName(ERole.valueOf(roleName.toUpperCase())).orElse(null);
                    if (role != null) {
                        roles.add(role);
                    } else {
                        return ResponseEntity.badRequest().body(null); // Role not found
                    }
                }
            }
            existingUser.setRoles(roles);

            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setUsername(updatedUser.getUsername());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            User updated = userRepository.save(existingUser);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<?> deleteAccount(Long id) {
        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            existingUser.setActive(false);
            userRepository.save(existingUser);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getAccountById(Long id) {
        if (userRepository.existsById(id) && userRepository.findById(id).isPresent()) {
            return ResponseEntity.ok().body(userRepository.findById(id).get().getRoles());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
