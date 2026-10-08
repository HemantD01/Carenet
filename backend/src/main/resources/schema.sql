-- ══════════════════════════════════════════════════════
--  CareNet – MySQL Database Setup Script
--  Run this ONCE before starting the Spring Boot app.
-- ══════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS carenet_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE carenet_db;

CREATE TABLE IF NOT EXISTS patients (
    patient_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255)        NOT NULL,
    abha_id         VARCHAR(50)  UNIQUE NOT NULL,
    phone           VARCHAR(20)  UNIQUE NOT NULL,
    email           VARCHAR(255) UNIQUE,
    password        VARCHAR(255)        NOT NULL,
    age             INT,
    gender          ENUM('MALE','FEMALE','OTHER'),
    blood_group     VARCHAR(5),
    allergies_notes TEXT,
    created_at      DATETIME            NOT NULL,
    updated_at      DATETIME            NOT NULL
);

CREATE TABLE IF NOT EXISTS doctors (
    doctor_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255)        NOT NULL,
    doctor_code         VARCHAR(100) UNIQUE NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL,
    password            VARCHAR(255)        NOT NULL,
    specialization      VARCHAR(255)        NOT NULL,
    hospital            VARCHAR(255),
    hospital_id         VARCHAR(100),
    medical_license_no  VARCHAR(100),
    location            VARCHAR(255),
    experience_years    INT,
    phone               VARCHAR(20),
    rating              DOUBLE DEFAULT 0.0,
    total_ratings       INT    DEFAULT 0,
    consultation_fee    INT,
    bio                 TEXT,
    available           BOOLEAN DEFAULT TRUE,
    created_at          DATETIME NOT NULL,
    updated_at          DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id       BIGINT NOT NULL,
    doctor_id        BIGINT NOT NULL,
    appointment_date DATE   NOT NULL,
    appointment_time TIME   NOT NULL,
    type             ENUM('NEW_PATIENT','FOLLOW_UP','CONSULTATION','EMERGENCY') NOT NULL,
    status           ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED','ONLINE') NOT NULL DEFAULT 'PENDING',
    notes            TEXT,
    created_at       DATETIME NOT NULL,
    updated_at       DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id)  REFERENCES doctors(doctor_id)   ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medical_records (
    record_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id       BIGINT NOT NULL,
    doctor_id        BIGINT NOT NULL,
    patient_abha_id  VARCHAR(50) NOT NULL,
    record_type      ENUM('BLOOD_TEST','PRESCRIPTION','ECG','XRAY_RADIOLOGY',
                          'DIAGNOSIS_REPORT','DISCHARGE_SUMMARY','MRI','THYROID_PANEL','OTHER') NOT NULL,
    report_file_name VARCHAR(500),
    report_file_path VARCHAR(1000),
    diagnosis        TEXT,
    prescription     TEXT,
    notes            TEXT,
    upload_date      DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id)  REFERENCES doctors(doctor_id)   ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_patients_abha ON patients(abha_id);
CREATE INDEX IF NOT EXISTS idx_records_abha  ON medical_records(patient_abha_id);
CREATE INDEX IF NOT EXISTS idx_appt_patient  ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appt_doctor   ON appointments(doctor_id);
CREATE INDEX IF NOT EXISTS idx_appt_date     ON appointments(appointment_date);
