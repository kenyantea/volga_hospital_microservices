package com.example.demoauth.service;

import com.example.demoauth.models.Doctor;
import com.example.demoauth.pojo.response.DoctorResponse;
import com.example.demoauth.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    @Autowired
    DoctorRepository doctorRepository;


    public ResponseEntity<?> getDoctors(String nameFilter, int from, int count) {
        Pageable pageable = PageRequest.of(from / count, count);
        Page<Doctor> doctors;

        if (nameFilter != null) {
            doctors = doctorRepository.findByUserFirstNameOrLastNameContaining(nameFilter, pageable);
        } else {
            doctors = doctorRepository.findAll(pageable);
        }

        Page<DoctorResponse> doctorResponses = doctors.map(doctor -> new DoctorResponse(
                doctor.getUser().getLastName(),
                doctor.getUser().getFirstName(),
                doctor.getUser().getUsername(),
                doctor.getSpeciality()));

        return ResponseEntity.ok(doctorResponses);
    }



    public ResponseEntity<?> getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElse(null);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new DoctorResponse(
                doctor.getUser().getLastName(),
                doctor.getUser().getFirstName(),
                doctor.getUser().getUsername(),
                doctor.getSpeciality()));
    }
}
