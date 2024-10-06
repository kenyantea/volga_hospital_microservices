package com.example.demoauth.repository;

import com.example.demoauth.models.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("SELECT d FROM Doctor d WHERE d.user.firstName LIKE %?1% OR d.user.lastName LIKE %?1%")
    Page<Doctor> findByUserFirstNameOrLastNameContaining(String name, Pageable pageable);
}

