package com.example.documents.controller;

import com.example.documents.model.History;
import com.example.documents.pojo.request.HistoryRequest;
import com.example.documents.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/History")
@Tag(name="History Controller", description="One and only controller for accessing history entries")
public class HistoryController {
    @Autowired
    HistoryService historyService;

    @Operation(summary="Get history entries by patient's id", description="Can be accessed by the patient and doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @GetMapping("/Account/{id}")
    public ResponseEntity<?> getHistoryByAccountId(@PathVariable Long id,
                                                   @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || historyService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }
        try {
            List<History> history = historyService.getHistoryByAccountId(id, token);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary="Get history entries by its id", description="Can be accessed by the patient and doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getHistoryById(@PathVariable Long id,
                                            @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || historyService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        try {
            History history = historyService.getHistoryById(id, token);
            if (history == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary="Create new history entry", description="Can be accessed by admins, doctors, and doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @PostMapping
    public ResponseEntity<?> createHistory(@Valid @RequestBody HistoryRequest history,
                                           @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || historyService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        try {
            historyService.createHistory(history, token);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary="Update history entry by its id", description="Can be accessed by admins, managers, and doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "No JWT token for auth."),
            @ApiResponse(responseCode = "403", description = "Method Forbidden.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHistory(@PathVariable Long id,
                                           @Valid @RequestBody HistoryRequest history,
                                           @Schema(hidden = true) @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || historyService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }
        try {
            historyService.updateHistory(id, history, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
