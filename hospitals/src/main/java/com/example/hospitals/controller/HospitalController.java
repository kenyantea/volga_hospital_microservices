package com.example.hospitals.controller;

import com.example.hospitals.model.Hospital;
import com.example.hospitals.model.Room;
import com.example.hospitals.pojo.request.CreateHospitalRequest;
import com.example.hospitals.service.HospitalService;
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
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.StreamSupport;

@Tag(name = "History Controller", description = "One and only controller for the Hospital Microservice")
@RestController
@RequestMapping("/api/Hospitals")
@SecurityRequirement(name = "JWT")
public class HospitalController {
    @Autowired
    HospitalService hospitalService;

    private final RestTemplate restTemplate = new RestTemplate();

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @Operation(summary="Get hospitals", description="Can be accessed by the authenticated users. " +
            "There are two parameters: from and count, defaults are 0 for \"from\" and 10 for \"count\"")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth.")
    })
    @GetMapping
    public ResponseEntity<?> getHospitals(
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "count", required = false, defaultValue = "10") int count,
            @Schema(hidden = true) @RequestHeader(name = "Authorization", required = false) String token) {

        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return hospitalService.getHospitals(from, count);
    }


    @Operation(summary="Get hospital by its id", description="Can be accessed by all the authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getHospitalById(@PathVariable Long id,
                                             @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Hospital hospital = hospitalService.getHospitalById(id);
        if (hospital == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hospital);
    }

    @Operation(summary="Get rooms by hospital's id", description="Can be accessed by all the authorized users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/Rooms")
    public ResponseEntity<?> getRoomsByHospitalId(@PathVariable Long id, @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> rooms = hospitalService.getRoomsByHospitalId(id);
        if (rooms == null || rooms.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary="Create new hospital", description="Can be accessed by admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created."),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @PostMapping
    public ResponseEntity<?> createHospital(@RequestBody CreateHospitalRequest newHospital,
                                            @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Update hospital's info by its id", description="Can be accessed by admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHospital(@PathVariable Long id, @RequestBody CreateHospitalRequest hospitalRequest,
                                                   @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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

    @Operation(summary="Delete hospital by its id", description="Can be accessed by admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Check parameters and body."),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHospital(@PathVariable Long id,
                                               @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
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
        String url = "http://accounts:8080/api/Authentication/Validate?accessToken=" +
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
