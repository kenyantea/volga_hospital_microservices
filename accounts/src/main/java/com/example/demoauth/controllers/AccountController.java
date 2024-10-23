package com.example.demoauth.controllers;

import com.example.demoauth.models.User;
import com.example.demoauth.pojo.request.UpdateUserRequest;
import com.example.demoauth.pojo.request.UserRequest;
import com.example.demoauth.service.AccountService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Proceeds info about accounts", tags = {"Account Controller"})
@RestController
@RequestMapping("/api/Accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {
    @Autowired
    AccountService accountService;

    @ApiOperation(value = "Info about current account",
            authorizations = {@Authorization(value="JWT")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success."),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in first.")
    })
    @GetMapping("/Me")
    public ResponseEntity<?> currentUser(@ApiIgnore Authentication authentication) {
        //System.out.println(token);
        return accountService.currentUser(authentication);
    }

    @ApiOperation(value = "Update current account's info", notes = "Last name, first name, and password can be updated")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success."),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in")
    })
    @PutMapping("/Update")
    public ResponseEntity<?> updateAccount(@ApiIgnore Authentication authentication,
                                                @RequestBody UpdateUserRequest updatedInfo) {
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

    @ApiOperation(value = "Get accounts", notes = "Can be accessed by admin only. Gets [count] accounts from [from]-th account.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in"),
            @ApiResponse(code = 403, message = "You'll have to be logged in as an admin")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllAccounts(@RequestParam(value = "from", defaultValue = "0") int from,
                                                     @RequestParam(value = "count", defaultValue = "10") int count) {

        return accountService.getAccounts(from, count);
    }

    @ApiIgnore
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @ApiOperation(value = "Create new account (admin)", notes = "Can be accessed by admin only. All fields can be accessed")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created."),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in."),
            @ApiResponse(code = 403, message = "You'll have to be logged in as an admin.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody UserRequest newUser) {
        return accountService.createUser(newUser);
    }

    @ApiOperation(value = "Update account (by admin)", notes = "Can be accessed by admin only. All fields can be updated")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in"),
            @ApiResponse(code = 403, message = "You'll have to be logged in as an admin")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccountByAdmin(@PathVariable Long id, @RequestBody UserRequest updatedUser) {
        return accountService.updateByAdmin(id, updatedUser);
    }

    @ApiOperation(value = "Delete account", notes = "Can be accessed by admin only. Soft deletes account.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content. Successfully deleted"),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in"),
            @ApiResponse(code = 403, message = "You'll have to be logged in as an admin")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }
}
