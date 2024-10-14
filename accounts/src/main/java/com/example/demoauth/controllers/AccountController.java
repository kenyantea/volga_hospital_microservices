package com.example.demoauth.controllers;

import com.example.demoauth.models.User;
import com.example.demoauth.pojo.request.UserRequest;
import com.example.demoauth.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/Accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping("/Me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        return accountService.currentUser(authentication);
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateAccount(Authentication authentication,
                                                @RequestBody User updatedInfo) {
        try {
            User updatedUser = accountService.updateUser(authentication, updatedInfo);
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok("Updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllAccounts(@RequestParam(value = "from", defaultValue = "0") int from,
                                                     @RequestParam(value = "count", defaultValue = "10") int count) {

        return accountService.getAccounts(from, count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody UserRequest newUser) {
        return accountService.createUser(newUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccountByAdmin(@PathVariable Long id, @RequestBody UserRequest updatedUser) {
        return accountService.updateByAdmin(id, updatedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }
}
