package com.carenet.controller;

import com.carenet.dto.*;
import com.carenet.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/patient/register
    @PostMapping("/patient/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerPatient(
            @RequestBody PatientRegisterRequest request) {
        AuthResponse auth = authService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Patient registered successfully.", auth));
    }

    // POST /api/auth/patient/login
    @PostMapping("/patient/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginPatient(
            @RequestBody PatientLoginRequest request) {
        AuthResponse auth = authService.loginPatient(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", auth));
    }

    // POST /api/auth/doctor/register
    @PostMapping("/doctor/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerDoctor(
            @RequestBody DoctorRegisterRequest request) {
        AuthResponse auth = authService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Doctor registered successfully.", auth));
    }

    // POST /api/auth/doctor/login
    @PostMapping("/doctor/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginDoctor(
            @RequestBody DoctorLoginRequest request) {
        AuthResponse auth = authService.loginDoctor(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", auth));
    }
}
