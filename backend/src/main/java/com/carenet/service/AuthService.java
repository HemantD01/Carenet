package com.carenet.service;

import com.carenet.dto.*;
import com.carenet.exception.BadRequestException;
import com.carenet.exception.UnauthorizedException;
import com.carenet.model.Doctor;
import com.carenet.model.Patient;
import com.carenet.repository.DoctorRepository;
import com.carenet.repository.PatientRepository;
import com.carenet.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PatientRepository patientRepository;
    private final DoctorRepository  doctorRepository;
    private final PasswordEncoder   passwordEncoder;
    private final JwtUtil           jwtUtil;

    public AuthService(PatientRepository patientRepository,
                       DoctorRepository doctorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.patientRepository = patientRepository;
        this.doctorRepository  = doctorRepository;
        this.passwordEncoder   = passwordEncoder;
        this.jwtUtil           = jwtUtil;
    }

    // ── Patient Register ──────────────────────────────────────────────────────

    @Transactional
    public AuthResponse registerPatient(PatientRegisterRequest req) {
        if (patientRepository.existsByAbhaId(req.abhaId))
            throw new BadRequestException("ABHA ID is already registered.");
        if (patientRepository.existsByPhone(req.phone))
            throw new BadRequestException("Phone number is already registered.");
        if (req.email != null && patientRepository.existsByEmail(req.email))
            throw new BadRequestException("Email is already registered.");

        Patient patient = Patient.builder()
                .name(req.name)
                .abhaId(req.abhaId)
                .phone(req.phone)
                .email(req.email)
                .password(passwordEncoder.encode(req.password))
                .age(req.age)
                .gender(req.gender != null
                        ? Patient.Gender.valueOf(req.gender.toUpperCase()) : null)
                .bloodGroup(req.bloodGroup)
                .build();

        patient = patientRepository.save(patient);
        String token = jwtUtil.generateToken(patient.getPatientId(), "PATIENT");
        return new AuthResponse(token, "PATIENT", patient.getPatientId(), patient.getName());
    }

    // ── Patient Login ─────────────────────────────────────────────────────────

    public AuthResponse loginPatient(PatientLoginRequest req) {
        Patient patient = patientRepository
                .findByPhoneOrAbhaId(req.identifier, req.identifier)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

        if (!passwordEncoder.matches(req.password, patient.getPassword()))
            throw new UnauthorizedException("Invalid credentials.");

        String token = jwtUtil.generateToken(patient.getPatientId(), "PATIENT");
        return new AuthResponse(token, "PATIENT", patient.getPatientId(), patient.getName());
    }

    // ── Doctor Register ───────────────────────────────────────────────────────

    @Transactional
    public AuthResponse registerDoctor(DoctorRegisterRequest req) {
        if (doctorRepository.existsByEmail(req.email))
            throw new BadRequestException("Email is already registered.");
        if (doctorRepository.existsByDoctorCode(req.doctorCode))
            throw new BadRequestException("Doctor ID is already registered.");

        Doctor doctor = Doctor.builder()
                .name(req.name)
                .doctorCode(req.doctorCode)
                .email(req.email)
                .password(passwordEncoder.encode(req.password))
                .specialization(req.specialization)
                .hospitalId(req.hospitalId)
                .medicalLicenseNo(req.medicalLicenseNo)
                .hospital(req.hospital != null ? req.hospital : "")
                .location(req.location)
                .experienceYears(req.experienceYears)
                .consultationFee(req.consultationFee)
                .build();

        doctor = doctorRepository.save(doctor);
        String token = jwtUtil.generateToken(doctor.getDoctorId(), "DOCTOR");
        return new AuthResponse(token, "DOCTOR", doctor.getDoctorId(), doctor.getName());
    }

    // ── Doctor Login ──────────────────────────────────────────────────────────

    public AuthResponse loginDoctor(DoctorLoginRequest req) {
        Doctor doctor = doctorRepository
                .findByEmailAndDoctorCode(req.email, req.doctorCode)
                .orElseThrow(() -> new UnauthorizedException("Invalid Doctor ID or email."));

        if (!passwordEncoder.matches(req.password, doctor.getPassword()))
            throw new UnauthorizedException("Invalid credentials.");

        String token = jwtUtil.generateToken(doctor.getDoctorId(), "DOCTOR");
        return new AuthResponse(token, "DOCTOR", doctor.getDoctorId(), doctor.getName());
    }
}
