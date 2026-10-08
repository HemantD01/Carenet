package com.carenet.service;

import com.carenet.dto.*;
import com.carenet.exception.BadRequestException;
import com.carenet.exception.ResourceNotFoundException;
import com.carenet.model.Appointment;
import com.carenet.model.Appointment.AppointmentStatus;
import com.carenet.model.Doctor;
import com.carenet.model.Patient;
import com.carenet.repository.AppointmentRepository;
import com.carenet.repository.DoctorRepository;
import com.carenet.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository     patientRepository;
    private final DoctorRepository      doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository     = patientRepository;
        this.doctorRepository      = doctorRepository;
    }

    // ── Book Appointment ──────────────────────────────────────────────────────

    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, BookAppointmentRequest req) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found."));

        Doctor doctor = doctorRepository.findById(req.doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        if (req.date.isBefore(LocalDate.now()))
            throw new BadRequestException("Appointment date cannot be in the past.");

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(req.date)
                .appointmentTime(req.time)
                .type(req.type != null ? req.type : Appointment.AppointmentType.CONSULTATION)
                .notes(req.notes)
                .build();

        appointment = appointmentRepository.save(appointment);
        return toResponse(appointment);
    }

    // ── Get Patient Appointments ──────────────────────────────────────────────

    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository
                .findByPatient_PatientIdOrderByAppointmentDateDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get Doctor Appointments ───────────────────────────────────────────────

    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        return appointmentRepository
                .findByDoctor_DoctorIdOrderByAppointmentDateAsc(doctorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get Doctor Today Appointments ─────────────────────────────────────────

    public List<AppointmentResponse> getDoctorTodayAppointments(Long doctorId) {
        return appointmentRepository
                .findByDoctor_DoctorIdAndAppointmentDate(doctorId, LocalDate.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Doctor Updates Status ─────────────────────────────────────────────────

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId,
                                            Long doctorId,
                                            UpdateStatusRequest req) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found."));

        if (!appt.getDoctor().getDoctorId().equals(doctorId))
            throw new BadRequestException("You can only update your own appointments.");

        appt.setStatus(req.status);
        return toResponse(appointmentRepository.save(appt));
    }

    // ── Patient Cancels ───────────────────────────────────────────────────────

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found."));

        if (!appt.getPatient().getPatientId().equals(patientId))
            throw new BadRequestException("You can only cancel your own appointments.");

        if (appt.getStatus() == AppointmentStatus.COMPLETED)
            throw new BadRequestException("Cannot cancel a completed appointment.");

        appt.setStatus(AppointmentStatus.CANCELLED);
        return toResponse(appointmentRepository.save(appt));
    }

    // ── Count Today for Doctor ────────────────────────────────────────────────

    public long countTodayForDoctor(Long doctorId) {
        return appointmentRepository
                .countByDoctor_DoctorIdAndAppointmentDate(doctorId, LocalDate.now());
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .appointmentId(a.getAppointmentId())
                .patientName(a.getPatient().getName())
                .patientAbhaId(a.getPatient().getAbhaId())
                .doctorName(a.getDoctor().getName())
                .doctorSpecialization(a.getDoctor().getSpecialization())
                .hospital(a.getDoctor().getHospital())
                .date(a.getAppointmentDate())
                .time(a.getAppointmentTime())
                .type(a.getType().name())
                .status(a.getStatus().name())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
