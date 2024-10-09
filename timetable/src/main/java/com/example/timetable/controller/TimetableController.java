package com.example.timetable.controller;

import com.example.timetable.model.TimetableEntry;
import com.example.timetable.pojo.request.TimetableRequest;
import com.example.timetable.pojo.response.DoctorResponse;
import com.example.timetable.pojo.response.HospitalResponse;
import com.example.timetable.service.TimetableService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/Timetable")
public class TimetableController {
    @Autowired
    TimetableService timetableService;

    @PostMapping
    public ResponseEntity<?> createTimetableEntry(@RequestBody TimetableRequest timetableRequest,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators and managers can create appointments");
        }

        try {
            if (!timetableService.checkIds(timetableRequest.getHospitalId(),timetableRequest.getDoctorId(),
                    timetableRequest.getRoom(), token)) {
                return ResponseEntity.badRequest().body("Check that the doctor, hospital, and room do exist and are active.");
            }
            TimetableEntry timetableEntry = timetableService.createTimetableEntry(timetableRequest);
            System.out.println(timetableEntry);
            if (timetableEntry == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(timetableEntry.getId());
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTimetableEntry(@PathVariable Long id, @RequestBody TimetableRequest timetableRequest,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators and managers can update appointments");
        }

        try {
            if (!timetableService.checkIds(timetableRequest.getHospitalId(),timetableRequest.getDoctorId(),
                    timetableRequest.getRoom(),token)) {
                return ResponseEntity.badRequest().body("Check that the doctor, hospital, and room do exist and are active.");
            }
            TimetableEntry updatedTimetableEntry = timetableService.updateTimetableEntry(id, timetableRequest);
            if (updatedTimetableEntry == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedTimetableEntry.getId());
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTimetableEntry(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators and managers can delete appointments");
        }

        try {
            timetableService.deleteTimetableEntry(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/Doctor/{id}")
    public ResponseEntity<?> deleteTimetableEntriesByDoctor(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators and managers can delete appointments");
        }

        try {
            ResponseEntity<String> doctorResponse = timetableService.getDoctorDataById(id, token);
            if (!doctorResponse.getStatusCode().is2xxSuccessful() || !timetableService.isDoctorActive(doctorResponse.getBody())) {
                return ResponseEntity.badRequest().body("This doctor doesn't exist");
            }

            timetableService.deleteTimetableEntriesByDoctor(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/Hospital/{id}")
    public ResponseEntity<?> deleteTimetableEntriesByHospital(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators and managers can delete appointments");
        }

        try {
            ResponseEntity<String> hospitalResponse = timetableService.getHospitalDataById(id, token);
            if (!hospitalResponse.getStatusCode().is2xxSuccessful()
                    || !timetableService.isHospitalActive(hospitalResponse.getBody())) {
                return ResponseEntity.badRequest().body("This hospital doesn't exist.");
            }

            timetableService.deleteTimetableEntriesByHospital(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/Hospital/{id}")
    public ResponseEntity<?> getTimetableByHospital(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        try {
            ResponseEntity<String> hospitalResponse = timetableService.getHospitalDataById(id, token);
            if (!hospitalResponse.getStatusCode().is2xxSuccessful()
                    || !timetableService.isHospitalActive(hospitalResponse.getBody())) {
                return ResponseEntity.badRequest().body("This hospital doesn't exist.");
            }
            return ResponseEntity.ok(timetableService.getTimetableByHospital(id, from, to));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/Doctor/{id}")
    public ResponseEntity<?> getTimetableByDoctor(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }


        try {
            ResponseEntity<String> doctorResponse = timetableService.getDoctorDataById(id, token);
            if (!doctorResponse.getStatusCode().is2xxSuccessful() || !timetableService.isDoctorActive(doctorResponse.getBody())) {
                return ResponseEntity.badRequest().body("This doctor doesn't exist");
            }
            return ResponseEntity.ok(timetableService.getTimetableByDoctor(id, from, to));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/Hospital/{id}/Room/{room}")
    public ResponseEntity<?> getTimetableByHospitalAndRoom(
            @PathVariable Long id,
            @PathVariable String room,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN")
                || role.contains("ROLE_MANAGER") || role.contains("ROLE_DOCTOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators, doctors, and managers can watch it.");
        }
        try {
            ResponseEntity<String> roomsResponse = timetableService.getRoomsForHospital(id, token);
            System.out.println(roomsResponse.getBody());
            if (!roomsResponse.getStatusCode().is2xxSuccessful() || !timetableService.isRoomInList(roomsResponse.getBody(), room)) {
                return ResponseEntity.badRequest().body("This room doesn't exist");
            }
            return ResponseEntity.ok(timetableService.getTimetableByHospitalAndRoom(id, room, from, to));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
