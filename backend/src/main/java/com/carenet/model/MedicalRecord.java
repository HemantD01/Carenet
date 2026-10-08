package com.carenet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private String patientAbhaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType recordType;

    private String reportFileName;
    private String reportFilePath;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }

    public enum RecordType {
        BLOOD_TEST, PRESCRIPTION, ECG, XRAY_RADIOLOGY,
        DIAGNOSIS_REPORT, DISCHARGE_SUMMARY, MRI, THYROID_PANEL, OTHER
    }

    // ── Getters ──
    public Long getRecordId()            { return recordId; }
    public Patient getPatient()          { return patient; }
    public Doctor getDoctor()            { return doctor; }
    public String getPatientAbhaId()     { return patientAbhaId; }
    public RecordType getRecordType()    { return recordType; }
    public String getReportFileName()    { return reportFileName; }
    public String getReportFilePath()    { return reportFilePath; }
    public String getDiagnosis()         { return diagnosis; }
    public String getPrescription()      { return prescription; }
    public String getNotes()             { return notes; }
    public LocalDateTime getUploadDate() { return uploadDate; }

    // ── Setters ──
    public void setRecordId(Long v)           { this.recordId = v; }
    public void setPatient(Patient v)         { this.patient = v; }
    public void setDoctor(Doctor v)           { this.doctor = v; }
    public void setPatientAbhaId(String v)    { this.patientAbhaId = v; }
    public void setRecordType(RecordType v)   { this.recordType = v; }
    public void setReportFileName(String v)   { this.reportFileName = v; }
    public void setReportFilePath(String v)   { this.reportFilePath = v; }
    public void setDiagnosis(String v)        { this.diagnosis = v; }
    public void setPrescription(String v)     { this.prescription = v; }
    public void setNotes(String v)            { this.notes = v; }
    public void setUploadDate(LocalDateTime v){ this.uploadDate = v; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MedicalRecord r = new MedicalRecord();
        public Builder patient(Patient v)         { r.patient = v; return this; }
        public Builder doctor(Doctor v)           { r.doctor = v; return this; }
        public Builder patientAbhaId(String v)    { r.patientAbhaId = v; return this; }
        public Builder recordType(RecordType v)   { r.recordType = v; return this; }
        public Builder reportFileName(String v)   { r.reportFileName = v; return this; }
        public Builder reportFilePath(String v)   { r.reportFilePath = v; return this; }
        public Builder diagnosis(String v)        { r.diagnosis = v; return this; }
        public Builder prescription(String v)     { r.prescription = v; return this; }
        public Builder notes(String v)            { r.notes = v; return this; }
        public MedicalRecord build()              { return r; }
    }
}
