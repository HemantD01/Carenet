package com.carenet.service;

import com.carenet.dto.*;
import com.carenet.exception.ResourceNotFoundException;
import com.carenet.model.Appointment;
import com.carenet.model.Patient;
import com.carenet.repository.AppointmentRepository;
import com.carenet.repository.MedicalRecordRepository;
import com.carenet.repository.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PatientService {

    private final PatientRepository       patientRepository;
    private final AppointmentRepository   appointmentRepository;
    private final MedicalRecordRepository recordRepository;
    private final PasswordEncoder         passwordEncoder;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          MedicalRecordRepository recordRepository,
                          PasswordEncoder passwordEncoder) {
        this.patientRepository     = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.recordRepository      = recordRepository;
        this.passwordEncoder       = passwordEncoder;
    }

    // ── Get profile ───────────────────────────────────────────────────────────

    public Patient getProfile(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found."));
    }

    // ── Update profile ────────────────────────────────────────────────────────

    @Transactional
    public Patient updateProfile(Long patientId, PatientRegisterRequest req) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found."));

        if (req.name       != null) patient.setName(req.name);
        if (req.age        != null) patient.setAge(req.age);
        if (req.bloodGroup != null) patient.setBloodGroup(req.bloodGroup);
        if (req.gender     != null)
            patient.setGender(Patient.Gender.valueOf(req.gender.toUpperCase()));
        if (req.password   != null && !req.password.isBlank())
            patient.setPassword(passwordEncoder.encode(req.password));

        return patientRepository.save(patient);
    }

    // ── Dashboard stats ───────────────────────────────────────────────────────

    public PatientDashboard getDashboard(Long patientId) {
        long totalRecords = recordRepository.countByPatient_PatientId(patientId);

        long upcoming = appointmentRepository
                .findByPatient_PatientIdOrderByAppointmentDateDesc(patientId)
                .stream()
                .filter(a -> !a.getAppointmentDate().isBefore(LocalDate.now()))
                .filter(a -> a.getStatus() != Appointment.AppointmentStatus.CANCELLED)
                .count();

        long doctorsConsulted = appointmentRepository
                .findByPatient_PatientIdOrderByAppointmentDateDesc(patientId)
                .stream()
                .map(a -> a.getDoctor().getDoctorId())
                .distinct().count();

        return PatientDashboard.builder()
                .totalRecords(totalRecords)
                .upcomingAppointments(upcoming)
                .totalDoctorsConsulted(doctorsConsulted)
                .build();
    }
}
