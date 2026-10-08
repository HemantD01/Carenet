package com.carenet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String doctorCode;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String specialization;

    private String hospital;
    private String hospitalId;
    private String medicalLicenseNo;
    private String location;
    private Integer experienceYears;
    private String phone;
    private Double rating;
    private Integer totalRatings;
    private Integer consultationFee;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private boolean available;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt    = LocalDateTime.now();
        updatedAt    = LocalDateTime.now();
        available    = true;
        rating       = 0.0;
        totalRatings = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Getters ──
    public Long getDoctorId()           { return doctorId; }
    public String getName()             { return name; }
    public String getDoctorCode()       { return doctorCode; }
    public String getEmail()            { return email; }
    public String getPassword()         { return password; }
    public String getSpecialization()   { return specialization; }
    public String getHospital()         { return hospital; }
    public String getHospitalId()       { return hospitalId; }
    public String getMedicalLicenseNo() { return medicalLicenseNo; }
    public String getLocation()         { return location; }
    public Integer getExperienceYears() { return experienceYears; }
    public String getPhone()            { return phone; }
    public Double getRating()           { return rating; }
    public Integer getTotalRatings()    { return totalRatings; }
    public Integer getConsultationFee() { return consultationFee; }
    public String getBio()              { return bio; }
    public boolean isAvailable()        { return available; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ── Setters ──
    public void setDoctorId(Long v)           { this.doctorId = v; }
    public void setName(String v)             { this.name = v; }
    public void setDoctorCode(String v)       { this.doctorCode = v; }
    public void setEmail(String v)            { this.email = v; }
    public void setPassword(String v)         { this.password = v; }
    public void setSpecialization(String v)   { this.specialization = v; }
    public void setHospital(String v)         { this.hospital = v; }
    public void setHospitalId(String v)       { this.hospitalId = v; }
    public void setMedicalLicenseNo(String v) { this.medicalLicenseNo = v; }
    public void setLocation(String v)         { this.location = v; }
    public void setExperienceYears(Integer v) { this.experienceYears = v; }
    public void setPhone(String v)            { this.phone = v; }
    public void setRating(Double v)           { this.rating = v; }
    public void setTotalRatings(Integer v)    { this.totalRatings = v; }
    public void setConsultationFee(Integer v) { this.consultationFee = v; }
    public void setBio(String v)              { this.bio = v; }
    public void setAvailable(boolean v)       { this.available = v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Doctor d = new Doctor();
        public Builder name(String v)             { d.name = v; return this; }
        public Builder doctorCode(String v)       { d.doctorCode = v; return this; }
        public Builder email(String v)            { d.email = v; return this; }
        public Builder password(String v)         { d.password = v; return this; }
        public Builder specialization(String v)   { d.specialization = v; return this; }
        public Builder hospital(String v)         { d.hospital = v; return this; }
        public Builder hospitalId(String v)       { d.hospitalId = v; return this; }
        public Builder medicalLicenseNo(String v) { d.medicalLicenseNo = v; return this; }
        public Builder location(String v)         { d.location = v; return this; }
        public Builder experienceYears(Integer v) { d.experienceYears = v; return this; }
        public Builder consultationFee(Integer v) { d.consultationFee = v; return this; }
        public Builder bio(String v)              { d.bio = v; return this; }
        public Doctor build()                     { return d; }
    }
}
