package com.example.timetable.controller;

import com.example.timetable.model.Appointment;
import com.example.timetable.model.TimetableEntry;
import com.example.timetable.pojo.request.AppointmentRequest;
import com.example.timetable.pojo.request.TimetableRequest;
import com.example.timetable.pojo.response.DoctorResponse;
import com.example.timetable.pojo.response.HospitalResponse;
import com.example.timetable.service.TimetableService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SecurityRequirement(name = "JWT")
@RestController
@Tag(name = "Timetable Controller", description = "Main controller for timetable entries & appointments")
@RequestMapping("/api/Timetable")
public class TimetableController {
    @Autowired
    TimetableService timetableService;

    @Operation(summary="Create new timetable entry", description="Can be accessed by admins and managers only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @PostMapping
    public ResponseEntity<?> createTimetableEntry(@RequestBody TimetableRequest timetableRequest,
                                                  @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Update timetable entry by its id", description="Can be accessed by admins and managers only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTimetableEntry(@PathVariable Long id, @RequestBody TimetableRequest timetableRequest,
                                                  @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Delete timetable entry by its id", description="Can be accessed by admins and managers only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTimetableEntry(@PathVariable Long id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Delete timetable entries of a doctor by doctor's id", description="Can be accessed by admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @DeleteMapping("/Doctor/{id}")
    public ResponseEntity<?> deleteTimetableEntriesByDoctor(@PathVariable Long id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Delete timetable entries of a hospital by hospital's id", description="Can be accessed by admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @DeleteMapping("/Hospital/{id}")
    public ResponseEntity<?> deleteTimetableEntriesByHospital(@PathVariable Long id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Get timetable entry by its id", description="Can be accessed by authenticated users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @GetMapping("/Hospital/{id}")
    public ResponseEntity<?> getTimetableByHospital(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Get doctor's timetable by id", description="Can be accessed by authenticated users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @GetMapping("/Doctor/{id}")
    public ResponseEntity<?> getTimetableByDoctor(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Get a room's timetable hospital's id and room", description="Can be accessed by admins, doctors, and managers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @GetMapping("/Hospital/{id}/Room/{room}")
    public ResponseEntity<?> getTimetableByHospitalAndRoom(
            @PathVariable Long id,
            @PathVariable String room,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (id == null || room == null) {
            return ResponseEntity.badRequest().body("Please add the path variables.");
        }

        if (!timetableService.isAuthenticated(token).stream().anyMatch(role -> role.contains("ROLE_ADMIN")
                || role.contains("ROLE_MANAGER") || role.contains("ROLE_DOCTOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators, doctors, and managers can watch it.");
        }

        try {
            ResponseEntity<String> roomsResponse = timetableService.getRoomsForHospital(id, token);

            if (roomsResponse.getStatusCode().is2xxSuccessful() && roomsResponse.getBody() != null) {
                if (timetableService.isRoomInList(roomsResponse.getBody(), room)) {
                    List<TimetableEntry> list = timetableService.getTimetableByHospitalAndRoom(id, room, from, to);
                    System.out.println(list);
                    if (list == null || list.isEmpty()) {
                        return ResponseEntity.ok("No rooms are available in this hospital.");
                    }
                    return ResponseEntity.ok(list);
                } else {
                    return ResponseEntity.badRequest().body("This room doesn't exist");
                }
            } else {
                return ResponseEntity.badRequest().body("Error fetching rooms for hospital.");
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary="Get available appointments by timetable id", description="Can be accessed by authorised users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @GetMapping("/{id}/Appointments")
    public ResponseEntity<?> getAvailableAppointments(@PathVariable Long id,
                                                      @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        try {
            List<?> availableAppointments = timetableService.getAppointments(id);
            if (availableAppointments != null && !availableAppointments.isEmpty()) {
                return ResponseEntity.ok(availableAppointments);
            }
            return ResponseEntity.ok("Nothing is available");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary="Create a new appointment", description="Can be accessed by authorised users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @PostMapping("/{id}/Appointments")
    public ResponseEntity<?> bookAppointment(@PathVariable Long id,
                                             @RequestBody AppointmentRequest appointmentRequest,
                                             @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        if (appointmentRequest.getTime() == null || appointmentRequest.getTime().isEmpty()) {
            return ResponseEntity.badRequest().body("Please choose time.");
        }

        if (!timetableService.isExistingTimetable(id)) {
            return ResponseEntity.badRequest().body("This timetable doesn't exist");
        }

        try {
            Long userId = timetableService.getUserIdFromToken(token);
            LocalDateTime time = LocalDateTime.parse(appointmentRequest.getTime(), DateTimeFormatter.ISO_DATE_TIME);
            timetableService.bookAppointment(id, new Appointment(time, userId, id));
            return ResponseEntity.ok("Appointment booked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
