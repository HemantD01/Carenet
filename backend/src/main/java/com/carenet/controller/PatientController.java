package com.carenet.controller;

import com.carenet.dto.*;
import com.carenet.model.Patient;
import com.carenet.service.AppointmentService;
import com.carenet.service.MedicalRecordService;
import com.carenet.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final PatientService       patientService;
    private final AppointmentService   appointmentService;
    private final MedicalRecordService medicalRecordService;

    public PatientController(PatientService patientService,
                             AppointmentService appointmentService,
                             MedicalRecordService medicalRecordService) {
        this.patientService       = patientService;
        this.appointmentService   = appointmentService;
        this.medicalRecordService = medicalRecordService;
    }

    private Long currentId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    // GET /api/patient/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<PatientDashboard>> dashboard(Authentication auth) {
        return ResponseEntity.ok(
                ApiResponse.ok("Dashboard loaded.", patientService.getDashboard(currentId(auth))));
    }

    // GET /api/patient/profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Patient>> getProfile(Authentication auth) {
        Patient patient = patientService.getProfile(currentId(auth));
        patient.setPassword(null);
        return ResponseEntity.ok(ApiResponse.ok("Profile loaded.", patient));
    }

    // PUT /api/patient/profile
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Patient>> updateProfile(
            Authentication auth,
            @RequestBody PatientRegisterRequest request) {
        Patient updated = patientService.updateProfile(currentId(auth), request);
        updated.setPassword(null);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated.", updated));
    }

    // GET /api/patient/records
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getRecords(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Records loaded.",
                medicalRecordService.getRecordsForPatient(currentId(auth))));
    }

    // POST /api/patient/appointments
    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            Authentication auth,
            @RequestBody BookAppointmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Appointment booked.",
                appointmentService.bookAppointment(currentId(auth), request)));
    }

    // GET /api/patient/appointments
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointments(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Appointments loaded.",
                appointmentService.getPatientAppointments(currentId(auth))));
    }

    // PATCH /api/patient/appointments/{id}/cancel
    @PatchMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            Authentication auth,
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(ApiResponse.ok("Appointment cancelled.",
                appointmentService.cancelAppointment(appointmentId, currentId(auth))));
    }
}
