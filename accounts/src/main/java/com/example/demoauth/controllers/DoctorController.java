package com.example.demoauth.controllers;

import com.example.demoauth.models.Doctor;
import com.example.demoauth.repository.DoctorRepository;
import com.example.demoauth.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(value = "Proceeds info about doctors", tags = {"Doctor Controller"})
@RestController
@RequestMapping("/api/Doctors")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @ApiOperation(value = "Get doctors", notes = "Search doctors using filter and parameters \"from\" and \"count\"")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in")
    })
    @GetMapping
    public ResponseEntity<?> getDoctors(@RequestParam(value = "nameFilter", required = false) String nameFilter,
                                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            return doctorService.getDoctors(nameFilter, from, count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Get doctor by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Doctor Not Found"),
            @ApiResponse(code = 400, message = "Bad Request (in some cases, you'll get a description)."),
            @ApiResponse(code = 401, message = "You'll have to be logged in")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        try {
            return doctorService.getDoctorById(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
