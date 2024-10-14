package com.example.documents.controller;

import com.example.documents.model.History;
import com.example.documents.service.HistoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/History")
public class HistoryController {
    @Autowired
    HistoryService historyService;

    @GetMapping("/Account/{id}")
    public ResponseEntity<?> getHistoryByAccountId(@PathVariable Long id,
                                                   @RequestHeader(value = "Authorization", required = false) String token) {
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getHistoryById(@PathVariable Long id,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
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

    @PostMapping
    public ResponseEntity<?> createHistory(@Valid @RequestBody History history,
                                                 @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || historyService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }

        try {
            History createdHistory = historyService.createHistory(history, token);
            return new ResponseEntity<>(createdHistory, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHistory(@PathVariable Long id, @Valid @RequestBody History history,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || historyService.isAuthenticated(token) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please sign in.");
        }
        try {
            History updatedHistory = historyService.updateHistory(id, history, token);
            return new ResponseEntity<>(updatedHistory, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
