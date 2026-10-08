package com.carenet.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status    = AppointmentStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AppointmentStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED, ONLINE }
    public enum AppointmentType   { NEW_PATIENT, FOLLOW_UP, CONSULTATION, EMERGENCY }

    // ── Getters ──
    public Long getAppointmentId()        { return appointmentId; }
    public Patient getPatient()           { return patient; }
    public Doctor getDoctor()             { return doctor; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public AppointmentType getType()      { return type; }
    public AppointmentStatus getStatus()  { return status; }
    public String getNotes()              { return notes; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    // ── Setters ──
    public void setAppointmentId(Long v)        { this.appointmentId = v; }
    public void setPatient(Patient v)           { this.patient = v; }
    public void setDoctor(Doctor v)             { this.doctor = v; }
    public void setAppointmentDate(LocalDate v) { this.appointmentDate = v; }
    public void setAppointmentTime(LocalTime v) { this.appointmentTime = v; }
    public void setType(AppointmentType v)      { this.type = v; }
    public void setStatus(AppointmentStatus v)  { this.status = v; }
    public void setNotes(String v)              { this.notes = v; }
    public void setCreatedAt(LocalDateTime v)   { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v)   { this.updatedAt = v; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Appointment a = new Appointment();
        public Builder patient(Patient v)           { a.patient = v; return this; }
        public Builder doctor(Doctor v)             { a.doctor = v; return this; }
        public Builder appointmentDate(LocalDate v) { a.appointmentDate = v; return this; }
        public Builder appointmentTime(LocalTime v) { a.appointmentTime = v; return this; }
        public Builder type(AppointmentType v)      { a.type = v; return this; }
        public Builder notes(String v)              { a.notes = v; return this; }
        public Appointment build()                  { return a; }
    }
}
