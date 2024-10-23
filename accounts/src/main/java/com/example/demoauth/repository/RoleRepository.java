package com.example.demoauth.repository;

import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByName(ERole name);
	boolean existsByName(ERole name);
}
