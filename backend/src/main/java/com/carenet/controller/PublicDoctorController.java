package com.carenet.controller;

import com.carenet.dto.*;
import com.carenet.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class PublicDoctorController {

    private final DoctorService doctorService;

    public PublicDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // GET /api/doctors/all
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {
        return ResponseEntity.ok(
                ApiResponse.ok("Doctors loaded.", doctorService.getAllDoctors()));
    }

    // GET /api/doctors/search?q=cardio
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> search(
            @RequestParam(defaultValue = "") String q) {
        List<DoctorResponse> results = q.isBlank()
                ? doctorService.getAllDoctors()
                : doctorService.searchDoctors(q);
        return ResponseEntity.ok(ApiResponse.ok("Search results.", results));
    }

    // GET /api/doctors/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Doctor loaded.", doctorService.getDoctorById(id)));
    }
}
