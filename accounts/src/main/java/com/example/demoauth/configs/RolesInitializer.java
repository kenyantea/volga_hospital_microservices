package com.example.demoauth.configs;


import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import com.example.demoauth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RolesInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        List<ERole> roles = Arrays.asList(ERole.ROLE_USER, ERole.ROLE_ADMIN, ERole.ROLE_DOCTOR, ERole.ROLE_MANAGER);

        for (ERole role : roles) {
            try {
                if (!roleRepository.existsByName(role)) {
                    Role newRole = new Role(role);
                    System.out.println(newRole.getName());
                    roleRepository.save(newRole);
                    System.out.println("Added new role: " + role);
                } else {
                    System.out.println("Role already exists: " + role);
                }
            } catch (Exception e) {
                System.err.println("Error adding role: " + role + " - " + e);
            }
        }
    }
}

