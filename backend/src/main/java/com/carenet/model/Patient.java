package com.carenet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String abhaId;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 5)
    private String bloodGroup;

    @Column(columnDefinition = "TEXT")
    private String allergiesNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Gender { MALE, FEMALE, OTHER }

    // ── Getters ──
    public Long getPatientId()          { return patientId; }
    public String getName()             { return name; }
    public String getAbhaId()           { return abhaId; }
    public String getPhone()            { return phone; }
    public String getEmail()            { return email; }
    public String getPassword()         { return password; }
    public Integer getAge()             { return age; }
    public Gender getGender()           { return gender; }
    public String getBloodGroup()       { return bloodGroup; }
    public String getAllergiesNotes()    { return allergiesNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ── Setters ──
    public void setPatientId(Long v)          { this.patientId = v; }
    public void setName(String v)             { this.name = v; }
    public void setAbhaId(String v)           { this.abhaId = v; }
    public void setPhone(String v)            { this.phone = v; }
    public void setEmail(String v)            { this.email = v; }
    public void setPassword(String v)         { this.password = v; }
    public void setAge(Integer v)             { this.age = v; }
    public void setGender(Gender v)           { this.gender = v; }
    public void setBloodGroup(String v)       { this.bloodGroup = v; }
    public void setAllergiesNotes(String v)   { this.allergiesNotes = v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    // ── Builder ──
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Patient p = new Patient();
        public Builder name(String v)          { p.name = v; return this; }
        public Builder abhaId(String v)        { p.abhaId = v; return this; }
        public Builder phone(String v)         { p.phone = v; return this; }
        public Builder email(String v)         { p.email = v; return this; }
        public Builder password(String v)      { p.password = v; return this; }
        public Builder age(Integer v)          { p.age = v; return this; }
        public Builder gender(Gender v)        { p.gender = v; return this; }
        public Builder bloodGroup(String v)    { p.bloodGroup = v; return this; }
        public Builder allergiesNotes(String v){ p.allergiesNotes = v; return this; }
        public Patient build()                 { return p; }
    }
}
