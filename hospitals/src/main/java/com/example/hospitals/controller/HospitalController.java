package com.example.hospitals.controller;

import com.example.hospitals.model.Hospital;
import com.example.hospitals.model.Room;
import com.example.hospitals.pojo.request.CreateHospitalRequest;
import com.example.hospitals.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/Hospitals")
public class HospitalController {
    @Autowired
    HospitalService hospitalService;

    private final RestTemplate restTemplate = new RestTemplate();

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hi!");
    }

    @GetMapping
    public ResponseEntity<?> getHospitals(
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "count", required = false, defaultValue = "10") int count,
            @RequestHeader("Authorization") String token) {

        if (isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return hospitalService.getHospitals(from, count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHospitalById(@PathVariable Long id,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing");
        }

        if (isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Hospital hospital = hospitalService.getHospitalById(id);
        if (hospital == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hospital);
    }

    @GetMapping("/{id}/Rooms")
    public ResponseEntity<?> getRoomsByHospitalId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> rooms = hospitalService.getRoomsByHospitalId(id);
        if (rooms == null || rooms.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<?> createHospital(@RequestBody CreateHospitalRequest newHospital,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can create hospitals");
        }

        Hospital hospital = hospitalService.createHospital(newHospital);
        if (hospital == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A hospital with this name already exists.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(hospital);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHospital(@PathVariable Long id, @RequestBody CreateHospitalRequest hospitalRequest,
                                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can create hospitals");
        }
        Hospital updatedHospital = hospitalService.updateHospital(id, hospitalRequest);
        if (updatedHospital == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedHospital);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHospital(@PathVariable Long id,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can create hospitals");
        }
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }

    private Set<String> isAuthenticated(String token) {
        String url = "http://localhost:8080/api/Authentication/Validate?accessToken=" +
                token.substring(7);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Set<String> roles = new HashSet<>();

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody != null && !responseBody.isEmpty()) {
                    String[] parts = responseBody.split(",");
                    roles.addAll(Arrays.asList(parts));
                    return roles;
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }
}
