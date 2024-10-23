package com.example.demoauth.configs;


import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import com.example.demoauth.models.User;
import com.example.demoauth.repository.RoleRepository;
import com.example.demoauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class RolesInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<ERole> roles = Arrays.asList(ERole.ROLE_USER, ERole.ROLE_ADMIN, ERole.ROLE_DOCTOR, ERole.ROLE_MANAGER);

        for (ERole role : roles) {
            try {
                if (!roleRepository.existsByName(role)) {
                    Role newRole = new Role(role);
                    roleRepository.save(newRole);
                    System.out.println("Added new role: " + role);
                } else {
                    System.out.println("Role already exists: " + role);
                }
            } catch (Exception e) {
                System.err.println("Error adding role: " + role + " - " + e);
            }
        }

        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElse(null);
        Role doctorRole = roleRepository.findByName(ERole.ROLE_DOCTOR).orElse(null);
        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER).orElse(null);
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElse(null);

        // Создание пользователей
        User user = new User("user", "user", "user", passwordEncoder.encode("user"), Set.of(userRole));
        User doctor = new User("doctor", "doctor", "doctor", passwordEncoder.encode("doctor"), Set.of(doctorRole));
        User manager = new User("manager", "manager", "manager", passwordEncoder.encode("manager"), Set.of(managerRole));
        User admin = new User("admin", "admin", "admin", passwordEncoder.encode("admin"), Set.of(adminRole));

        List<User> users = new ArrayList<>(List.of(user, doctor, manager, admin));

        for (User u : users) {
            try {
                if (!userRepository.existsByUsername(u.getUsername())) {
                    userRepository.save(u);
                    System.out.println("Added new user: " + u);
                } else {
                    System.out.println("User already exists: " + u);
                }
            } catch (Exception e) {
                System.err.println("Error user role: " + u + " - " + e);
            }
        }
    }




}

