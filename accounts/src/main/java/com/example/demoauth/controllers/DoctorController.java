package com.example.demoauth.controllers;

import com.example.demoauth.models.Doctor;
import com.example.demoauth.repository.DoctorRepository;
import com.example.demoauth.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/Doctors")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<?> getDoctors(@RequestParam(value = "nameFilter", required = false) String nameFilter,
                                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @RequestParam(value = "count", defaultValue = "10") int count) {
        return doctorService.getDoctors(nameFilter, from, count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }
}
