package com.example.timetable.controller;

import com.example.timetable.service.TimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT")
@Tag(name = "Appointment Controller")
@RestController
@RequestMapping("/api/Appointment")
public class AppointmentController {
    @Autowired
    TimetableService timetableService;

    @Operation(summary="Delete (cancel) appointment by its id", description="Can be accessed by the patient, managers, and admins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id,
                                               @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || timetableService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        try {
            timetableService.cancelAppointment(id, token);
            return ResponseEntity.ok("Appointment canceled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
