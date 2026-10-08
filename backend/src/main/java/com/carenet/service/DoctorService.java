package com.carenet.service;

import com.carenet.dto.*;
import com.carenet.exception.ResourceNotFoundException;
import com.carenet.model.Doctor;
import com.carenet.repository.AppointmentRepository;
import com.carenet.repository.DoctorRepository;
import com.carenet.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository        doctorRepository;
    private final AppointmentRepository   appointmentRepository;
    private final MedicalRecordRepository recordRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         MedicalRecordRepository recordRepository) {
        this.doctorRepository      = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.recordRepository      = recordRepository;
    }

    // ── Get all doctors (public) ──────────────────────────────────────────────

    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Search doctors (public) ───────────────────────────────────────────────

    public List<DoctorResponse> searchDoctors(String query) {
        return doctorRepository.searchDoctors(query)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get doctor by ID (public) ─────────────────────────────────────────────

    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));
        return toResponse(doctor);
    }

    // ── Doctor dashboard ─────────────────────────────────────────────────────

    public DoctorDashboard getDashboard(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        long todayAppts = appointmentRepository
                .countByDoctor_DoctorIdAndAppointmentDate(doctorId, LocalDate.now());

        long totalPatients = appointmentRepository
                .findByDoctor_DoctorIdOrderByAppointmentDateAsc(doctorId)
                .stream()
                .map(a -> a.getPatient().getPatientId())
                .distinct().count();

        long totalReports = recordRepository.countByDoctor_DoctorId(doctorId);

        return DoctorDashboard.builder()
                .todayAppointments(todayAppts)
                .totalPatients(totalPatients)
                .totalReportsUploaded(totalReports)
                .rating(doctor.getRating())
                .build();
    }

    // ── Update doctor profile ─────────────────────────────────────────────────

    @Transactional
    public DoctorResponse updateProfile(Long doctorId, DoctorRegisterRequest req) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        if (req.name           != null) doctor.setName(req.name);
        if (req.specialization != null) doctor.setSpecialization(req.specialization);
        if (req.hospital       != null) doctor.setHospital(req.hospital);
        if (req.location       != null) doctor.setLocation(req.location);
        if (req.experienceYears!= null) doctor.setExperienceYears(req.experienceYears);
        if (req.consultationFee!= null) doctor.setConsultationFee(req.consultationFee);

        return toResponse(doctorRepository.save(doctor));
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private DoctorResponse toResponse(Doctor d) {
        return DoctorResponse.builder()
                .doctorId(d.getDoctorId())
                .name(d.getName())
                .specialization(d.getSpecialization())
                .hospital(d.getHospital())
                .location(d.getLocation())
                .experienceYears(d.getExperienceYears())
                .rating(d.getRating())
                .totalRatings(d.getTotalRatings())
                .consultationFee(d.getConsultationFee())
                .bio(d.getBio())
                .available(d.isAvailable())
                .build();
    }
}
