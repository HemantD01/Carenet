package com.carenet.controller;

import com.carenet.dto.*;
import com.carenet.service.AppointmentService;
import com.carenet.service.DoctorService;
import com.carenet.service.MedicalRecordService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final DoctorService        doctorService;
    private final AppointmentService   appointmentService;
    private final MedicalRecordService medicalRecordService;

    public DoctorController(DoctorService doctorService,
                            AppointmentService appointmentService,
                            MedicalRecordService medicalRecordService) {
        this.doctorService        = doctorService;
        this.appointmentService   = appointmentService;
        this.medicalRecordService = medicalRecordService;
    }

    private Long currentId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    // GET /api/doctor/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DoctorDashboard>> dashboard(Authentication auth) {
        return ResponseEntity.ok(
                ApiResponse.ok("Dashboard loaded.", doctorService.getDashboard(currentId(auth))));
    }

    // GET /api/doctor/profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> getProfile(Authentication auth) {
        return ResponseEntity.ok(
                ApiResponse.ok("Profile loaded.", doctorService.getDoctorById(currentId(auth))));
    }

    // PUT /api/doctor/profile
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateProfile(
            Authentication auth,
            @RequestBody DoctorRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated.",
                doctorService.updateProfile(currentId(auth), request)));
    }

    // GET /api/doctor/appointments
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Appointments loaded.",
                appointmentService.getDoctorAppointments(currentId(auth))));
    }

    // GET /api/doctor/appointments/today
    @GetMapping("/appointments/today")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getTodayAppointments(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Today's appointments loaded.",
                appointmentService.getDoctorTodayAppointments(currentId(auth))));
    }

    // PATCH /api/doctor/appointments/{id}/status
    @PatchMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            Authentication auth,
            @PathVariable Long appointmentId,
            @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated.",
                appointmentService.updateStatus(appointmentId, currentId(auth), request)));
    }

    // POST /api/doctor/records  (multipart/form-data)
    @PostMapping(value = "/records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> uploadRecord(
            Authentication auth,
            @RequestPart("data") UploadRecordRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(ApiResponse.ok("Report uploaded and linked to patient.",
                medicalRecordService.uploadRecord(currentId(auth), request, file)));
    }

    // GET /api/doctor/records
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getMyRecords(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Records loaded.",
                medicalRecordService.getRecordsByDoctor(currentId(auth))));
    }

    // GET /api/doctor/records/patient/{abhaId}
    @GetMapping("/records/patient/{abhaId}")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getPatientRecords(
            @PathVariable String abhaId) {
        return ResponseEntity.ok(ApiResponse.ok("Patient records loaded.",
                medicalRecordService.getRecordsByAbhaId(abhaId)));
    }
}
